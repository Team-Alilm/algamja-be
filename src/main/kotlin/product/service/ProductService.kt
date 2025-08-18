package org.team_alilm.product.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.basket.repository.BasketQueryRepository
import org.team_alilm.basket.repository.BasketRepository
import org.team_alilm.common.exception.BusinessException
import org.team_alilm.common.exception.ErrorCode
import org.team_alilm.product.controller.v1.dto.param.ProductListParam
import org.team_alilm.product.controller.v1.dto.request.RegisterProductRequest
import org.team_alilm.product.controller.v1.dto.response.ProductCountResponse
import org.team_alilm.product.controller.v1.dto.response.ProductDetailResponse
import org.team_alilm.product.controller.v1.dto.response.ProductListResponse
import org.team_alilm.product.controller.v1.dto.response.ProductResponse
import org.team_alilm.product.controller.v1.dto.response.RecentlyRestockedProductListResponse
import org.team_alilm.product.controller.v1.dto.response.RecentlyRestockedProductResponse
import org.team_alilm.product.controller.v1.dto.response.SimilarProductListResponse
import org.team_alilm.product.controller.v1.dto.response.SimilarProductResponse
import org.team_alilm.product.crawler.CrawlerRegistry
import org.team_alilm.product.image.repository.ProductImageRepository
import org.team_alilm.product.repository.ProductQueryRepository
import org.team_alilm.product.repository.ProductRepository

@Service
@Transactional(readOnly = true)
class ProductService(
    private val productRepository: ProductRepository,
    private val productQueryRepository: ProductQueryRepository,
    private val productImageRepository: ProductImageRepository,
    private val basketRepository: BasketRepository,
    private val basketQueryRepository: BasketQueryRepository,
    private val crawlerRegistry: CrawlerRegistry,
) {

    fun getProductCount(): ProductCountResponse {
        return ProductCountResponse(productCount = productRepository.count())
    }

    fun getProductDetail(productId: Long) : ProductDetailResponse {
        val product = productRepository.findByIdOrNull(productId)
            ?: throw BusinessException(errorCode = ErrorCode.PRODUCT_NOT_FOUND)

        val productImageList = productImageRepository.findAllByProductId(productId).map { it.imageUrl }

        val waitingCount = basketRepository.countByProductId(productId)

        return ProductDetailResponse.from(
            product = product,
            imageUrls = productImageList,
            waitingCount = waitingCount
        )
    }

    @Transactional("exposedTxManager")
    fun getProductList(param : ProductListParam) : ProductListResponse {
        val slice = productQueryRepository.sliceProducts(param)
        if (slice.content.isEmpty()) {
            return ProductListResponse(productList = emptyList(), hasNext = false)
        }

        // 2) 대기수 집계 (해당 페이지의 상품 id 만)
        val ids = slice.content.map { it.id }
        val waitingMap: Map<Long, Long> = basketQueryRepository.fetchWaitingCounts(ids)

        // 3) 응답 매핑 (waitingCount 주입)
        val products = slice.content.map { productProjection ->
            ProductResponse.from(productProjection, waitingCount = waitingMap[productProjection.id] ?: 0L)
        }

        return ProductListResponse(productList = products, hasNext = slice.hasNext())
    }

    fun getSimilarProducts(productId: Long) : SimilarProductListResponse {
        val product = productRepository.findByIdOrNull(productId)
            ?: throw BusinessException(errorCode = ErrorCode.PRODUCT_NOT_FOUND)

        val productList = productRepository.findTop10ByIdNotAndFirstCategoryOrSecondCategoryAndIsDeleteFalseOrderByIdDesc(
            id = productId,
            firstCategory = product.firstCategory,
            secondCategory = product.secondCategory
        ) .ifEmpty {
            return SimilarProductListResponse(similarProductList = emptyList())

        }

        val similarProductList = productList.map {
            SimilarProductResponse.from(
                product = it,
            )
        }

        return SimilarProductListResponse(similarProductList = similarProductList)
    }

    @Transactional("exposedTxManager")
    fun getRecentlyRestockedProducts() : RecentlyRestockedProductListResponse {
        val ids = productQueryRepository.getTop10RecentlyNotifiedProductIds().ifEmpty {
            return RecentlyRestockedProductListResponse(recentlyRestockedProductResponseList = emptyList())
        }

        return productRepository.findAllById(ids)
            .map { product ->
                RecentlyRestockedProductResponse.from(
                    product = product
                )
            }
            .let { RecentlyRestockedProductListResponse(recentlyRestockedProductResponseList = it) }
    }

    @Transactional
    fun registerProduct(request: RegisterProductRequest) {
        crawlerRegistry.resolve(url = request.productUrl)
    }
}