package org.team_alilm.algamja.product.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.algamja.basket.repository.BasketExposedRepository
import org.team_alilm.algamja.common.enums.Sort
import org.team_alilm.algamja.common.enums.ProductCategory
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode
import org.team_alilm.algamja.product.controller.v1.dto.param.ProductListParam
import org.team_alilm.algamja.product.controller.v1.dto.param.ProductCountParam
import org.team_alilm.algamja.product.controller.v1.dto.request.ProductRegisterRequest
import org.team_alilm.algamja.product.controller.v1.dto.response.CrawlProductResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.ProductCountResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.ProductRegisterResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.DelayedProductResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.ProductDetailResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.ProductListResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.ProductResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.RecentlyRestockedProductListResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.RecentlyRestockedProductResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.SimilarProductListResponse
import org.team_alilm.algamja.product.controller.v1.dto.response.SimilarProductResponse
import org.team_alilm.algamja.product.crawler.CrawlerRegistry
import org.team_alilm.algamja.product.image.repository.ProductImageExposedRepository
import org.team_alilm.algamja.product.repository.ProductExposedRepository

@Service
@Transactional(readOnly = true)
class ProductService(
    private val productExposedRepository: ProductExposedRepository,
    private val basketExposedRepository: BasketExposedRepository,
    private val productImageExposedRepository: ProductImageExposedRepository,
    private val crawlerRegistry: CrawlerRegistry
) {

    private val log = LoggerFactory.getLogger(javaClass)

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
                firstCategory = ProductCategory.mapEnglishToKorean(row.firstCategory) ?: row.firstCategory,
                secondCategory = ProductCategory.mapEnglishToKorean(row.secondCategory) ?: row.secondCategory,
                firstOption = row.firstOption,
                secondOption = row.secondOption,
                thirdOption = row.thirdOption,
                waitingCount = waitingByProductId[row.id] ?: 0L
            )
        }

        // 6) 마지막 상품 정보 추출 (무한 스크롤용)
        val lastProduct = responses.lastOrNull()
        val lastProductId = lastProduct?.id
        val lastPrice = lastProduct?.price?.toInt()
        val lastWaitingCount = lastProduct?.waitingCount

        return ProductListResponse(
            productList = responses,
            hasNext = slice.hasNext,
            lastProductId = lastProductId,
            lastPrice = lastPrice,
            lastWaitingCount = lastWaitingCount
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
    fun crawlProduct(url: String) : CrawlProductResponse {
        val startTime = System.currentTimeMillis()
        log.info("Starting product crawling for URL: {}", url)
        
        return try {
            val productCrawler = crawlerRegistry.resolve(url = url)
            log.debug("Selected crawler: {} for URL: {}", productCrawler::class.simpleName, url)
            
            // 2. URL 정규화 (불필요한 파라미터, 리다이렉션 제거 등)
            val normalizedUrl = productCrawler.normalize(url)
            log.debug("URL normalized: {} -> {}", url, normalizedUrl)
            
            // 3. 크롤링 실행 → 상품 정보 얻기
            val crawledProduct = productCrawler.fetch(normalizedUrl)
            
            val response = CrawlProductResponse(
                number = crawledProduct.storeNumber, // 상품 번호
                name = crawledProduct.name, // 상품명
                brand = crawledProduct.brand, // 브랜드
                thumbnailUrl = crawledProduct.thumbnailUrl, // 썸네일
                imageUrlList = crawledProduct.imageUrls, // 이미지 리스트
                store = crawledProduct.store.name, // 스토어명
                price = crawledProduct.price, // 가격
                firstCategory = crawledProduct.firstCategory, // 1차 카테고리
                secondCategory = crawledProduct.secondCategory, // 2차 카테고리
                firstOptions = crawledProduct.firstOptions, // 1차 옵션
                secondOptions = crawledProduct.secondOptions, // 2차 옵션
                thirdOptions = crawledProduct.thirdOptions // 3차 옵션
            )
            
            val duration = System.currentTimeMillis() - startTime
            log.info("Successfully crawled product '{}' from {} in {}ms, categories: [{}, {}], options: [{}, {}, {}]",
                    crawledProduct.name,
                    crawledProduct.store,
                    duration,
                    crawledProduct.firstCategory,
                    crawledProduct.secondCategory,
                    crawledProduct.firstOptions.size,
                    crawledProduct.secondOptions.size,
                    crawledProduct.thirdOptions.size)
            
            response
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            log.error("Failed to crawl product from URL: {} after {}ms", url, duration, e)
            throw BusinessException(errorCode = ErrorCode.CRAWLER_INVALID_RESPONSE, cause = e)
        }
    }

    @Transactional
    fun getProductCount(param: ProductCountParam): ProductCountResponse {
        val count = productExposedRepository.countProducts(param)
        return ProductCountResponse(productCount = count)
    }

    /**
     * 회원별로 재입고가 가장 오래 지연된 상품 조회
     * - 장바구니에 추가했지만 아직 재입고 알림이 오지 않은 상품 중 가장 오래된 것
     */
    fun getMostDelayedProductByMember(memberId: Long): DelayedProductResponse? {
        log.info("Fetching most delayed restock product for member: {}", memberId)
        
        return try {
            // 1) 해당 회원의 가장 오래 대기 중인 장바구니 항목 조회
            val basketRow = basketExposedRepository.fetchOldestWaitingProductByMember(memberId)
                ?: run {
                    log.debug("No waiting products found for member: {}", memberId)
                    return null
                }
            
            // 2) 해당 상품 정보 조회
            val productRow = productExposedRepository.fetchProductById(basketRow.productId)
                ?: run {
                    log.warn("Product not found for basketRow productId: {} (member: {})", basketRow.productId, memberId)
                    return null
                }
            
            val response = DelayedProductResponse.from(productRow, basketRow)
            
            log.info("Found most delayed product for member {}: '{}' waiting {} days since {}", 
                    memberId, productRow.name, response.waitingDays, basketRow.createdDate)
            
            response
        } catch (e: Exception) {
            log.error("Failed to fetch most delayed product for member: {}", memberId, e)
            throw BusinessException(errorCode = ErrorCode.INTERNAL_ERROR, cause = e)
        }
    }

    @Transactional
    fun registerProduct(request: ProductRegisterRequest, memberId: Long): ProductRegisterResponse {
        log.info("Registering product: {} from store: {} for member: {}", request.name, request.store, memberId)
        
        // 1. 기존 상품이 있는지 확인
        val existingProduct = productExposedRepository.fetchProductByStoreNumber(
            storeNumber = request.number,
            store = request.store
        )
        
        val productId = if (existingProduct != null) {
            log.info("Product already exists with ID: {}", existingProduct.id)
            // 이미 상품이 존재하면 장바구니에만 추가
            existingProduct.id
        } else {
            // 새로운 상품 등록
            val savedProduct = productExposedRepository.save(
                name = request.name,
                storeNumber = request.number,
                brand = request.brand,
                thumbnailUrl = request.thumbnailUrl,
                store = request.store,
                price = request.price,
                firstCategory = request.firstCategory,
                secondCategory = request.secondCategory,
                firstOption = request.firstOption,
                secondOption = request.secondOption,
                thirdOption = request.thirdOption
            )
            
            // 상품 이미지 저장
            request.imageUrlList.forEach { imageUrl ->
                productImageExposedRepository.save(
                    productId = savedProduct.id,
                    imageUrl = imageUrl
                )
            }
            
            log.info("Successfully registered new product: {} with ID: {}", savedProduct.name, savedProduct.id)
            savedProduct.id
        }
        
        // 2. 장바구니 중복 체크
        val existingBasket = basketExposedRepository.fetchBasketByMemberIdAndProductId(
            memberId = memberId,
            productId = productId
        )
        
        if (existingBasket != null) {
            log.warn("Product already in basket for member: {} and product: {}", memberId, productId)
            throw BusinessException(ErrorCode.BASKET_ALREADY_EXISTS)
        }
        
        // 3. 장바구니에 추가
        val basketId = basketExposedRepository.createBasket(
            memberId = memberId,
            productId = productId
        )
        
        log.info("Successfully added product {} to basket with ID: {} for member: {}", productId, basketId, memberId)
        
        // 상품 정보 조회하여 응답 생성
        val product = existingProduct ?: productExposedRepository.fetchProductById(productId)
            ?: throw BusinessException(ErrorCode.PRODUCT_NOT_FOUND)
        
        return ProductRegisterResponse(
            productId = product.id,
            productName = product.name
        )
    }
}