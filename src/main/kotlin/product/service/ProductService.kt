package org.team_alilm.product.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.basket.repository.BasketExposedRepository
import org.team_alilm.common.enums.Sort
import org.team_alilm.common.exception.BusinessException
import common.exception.ErrorCode
import org.team_alilm.product.controller.v1.dto.param.ProductListParam
import org.team_alilm.product.controller.v1.dto.response.CrawlProductResponse
import org.team_alilm.product.controller.v1.dto.response.ProductCountResponse
import org.team_alilm.product.controller.v1.dto.response.ProductDetailResponse
import org.team_alilm.product.controller.v1.dto.response.ProductListResponse
import org.team_alilm.product.controller.v1.dto.response.ProductResponse
import org.team_alilm.product.controller.v1.dto.response.RecentlyRestockedProductListResponse
import org.team_alilm.product.controller.v1.dto.response.RecentlyRestockedProductResponse
import org.team_alilm.product.controller.v1.dto.response.SimilarProductListResponse
import org.team_alilm.product.controller.v1.dto.response.SimilarProductResponse
import org.team_alilm.product.crawler.CrawlerRegistry
import org.team_alilm.product.image.repository.ProductImageExposedRepository
import org.team_alilm.product.repository.ProductExposedRepository

@Service
@Transactional(readOnly = true)
class ProductService(
    private val productExposedRepository: ProductExposedRepository,
    private val basketExposedRepository: BasketExposedRepository,
    private val productImageExposedRepository: ProductImageExposedRepository,
    private val crawlerRegistry: CrawlerRegistry
) {

    fun getProductDetail(productId: Long) : ProductDetailResponse {
        // 1) 상품 정보 조회
        val productRow = productExposedRepository.fetchProductById(productId)
            ?: throw BusinessException(errorCode = ErrorCode.PRODUCT_NOT_FOUND)

        // 2) 상품 이미지 조회
        val productImageList = productImageExposedRepository.fetchProductImageById(productRow.id)

        // 3) 상품 대기수 집계
        val waitingCount = basketExposedRepository.fetchWaitingCount(productId)

        return ProductDetailResponse.from(
            productRow = productRow,
            imageUrls = productImageList.map {  it.imageUrl },
            waitingCount = waitingCount
        )
    }

    fun getProductList(param: ProductListParam): ProductListResponse {
        // 1) 정렬 분기: 인기순(대기자수 내림차순) vs 일반 정렬
        val slice = when (param.sort) {
            Sort.WAITING_COUNT_DESC -> productExposedRepository.fetchProductsOrderByWaitingCountDesc(param)
            else                    -> productExposedRepository.fetchProducts(param)
        }

        val productRows = slice.productRows
        if (productRows.isEmpty()) {
            return ProductListResponse(productList = emptyList(), hasNext = false)
        }

        // 2) 이번 페이지의 상품 id만 추출(중복 제거)
        val productIds = productRows.asSequence().map { it.id }.distinct().toList()

        // 3) 이미지 한번에 조회 → productId -> List<url>
        val imagesByProductId: Map<Long, List<String>> =
            productImageExposedRepository
                .fetchProductImagesByProductIds(productIds)
                .groupBy({ it.productId }, { it.imageUrl })

        // 4) 대기수 집계 한번에 조회 → productId -> count
        //    (※ fetchProductsOrderByWaitingCountDesc 에서 이미 waitingCount를 담아온다면
        //       여기 호출을 생략하고 productRows에서 꺼내 쓰면 됨)
        val waitingByProductId: Map<Long, Long> =
            basketExposedRepository
                .fetchWaitingCounts(productIds)
                .associate { wc -> wc.productId to wc.waitingCount }

        // 5) 매핑
        val responses = productRows.map { row ->
            ProductResponse(
                id = row.id,
                name = row.name,
                brand = row.brand,
                thumbnailUrl = imagesByProductId[row.id]?.firstOrNull() ?: row.thumbnailUrl,
                store = row.store.name,
                price = row.price.toLong(),
                firstCategory = row.firstCategory,
                secondCategory = row.secondCategory,
                firstOption = row.firstOption,
                secondOption = row.secondOption,
                thirdOption = row.thirdOption,
                waitingCount = waitingByProductId[row.id] ?: 0L
            )
        }

        return ProductListResponse(
            productList = responses,
            hasNext = slice.hasNext
        )
    }

    fun getSimilarProducts(productId: Long): SimilarProductListResponse {
        val product = productExposedRepository.fetchProductById(productId)
            ?: throw BusinessException(errorCode = ErrorCode.PRODUCT_NOT_FOUND)

        val productList = productExposedRepository.fetchTop10SimilarProducts(
            excludeId = productId,
            firstCategory = product.firstCategory,
            secondCategory = product.secondCategory,
        ).ifEmpty {
            return SimilarProductListResponse(similarProductList = emptyList())
        }

        val similarProductList = productList.map {
            SimilarProductResponse(
                productId = it.id,
                name = it.name,
                brand = it.brand,
                thumbnailUrl = it.thumbnailUrl
            )
        }
        return SimilarProductListResponse(similarProductList = similarProductList)
    }


    fun getRecentlyRestockedProducts(): RecentlyRestockedProductListResponse {
        val ids = basketExposedRepository.fetchTop10RecentlyNotifiedProductIds().ifEmpty {
            return RecentlyRestockedProductListResponse(recentlyRestockedProductResponseList = emptyList())
        }

        val products = productExposedRepository.fetchProductsByIds(ids)
        val responses = products.map { product ->
            RecentlyRestockedProductResponse(
                productId = product.id,
                name = product.name,
                brand = product.brand,
                thumbnailUrl = product.thumbnailUrl
            )
        }

        return RecentlyRestockedProductListResponse(recentlyRestockedProductResponseList = responses)
    }

    @Transactional
    fun crawlProduct(productUrl: String) : CrawlProductResponse {
        val productCrawler = crawlerRegistry.resolve(url = productUrl)
        // 2. URL 정규화 (불필요한 파라미터, 리다이렉션 제거 등)
        val normalizedUrl = productCrawler.normalize(productUrl)
        // 3. 크롤링 실행 → 상품 정보 얻기
        val crawledProduct = productCrawler.fetch(normalizedUrl)

        return CrawlProductResponse(
            number = crawledProduct.storeNumber, // 상품 번호
            name = crawledProduct.name, // 상품명
            brand = crawledProduct.brand, // 브랜드
            thumbnailUrl = crawledProduct.thumbnailUrl, // 썸네일
            imageUrlList = crawledProduct.imageUrls, // 이미지 리스트
            store = crawledProduct.store, // 스토어명
            price = crawledProduct.price, // 가격
            firstCategory = crawledProduct.firstCategory, // 1차 카테고리
            secondCategory = crawledProduct.secondCategory, // 2차 카테고리
            firstOptions = crawledProduct.firstOptions, // 1차 옵션
            secondOptions = crawledProduct.secondOptions, // 2차 옵션
            thirdOptions = crawledProduct.thirdOptions // 3차 옵션
        )
    }

    @Transactional
    fun getProductCount(param: ProductListParam): ProductCountResponse {
        val count = productExposedRepository.countProducts(param)
        return ProductCountResponse(productCount = count)
    }
}