package org.team_alilm.product.service

import org.springframework.stereotype.Service
import org.team_alilm.common.exception.BusinessException
import org.team_alilm.common.exception.ErrorCode
import org.team_alilm.product.controller.dto.param.ProductListParam
import org.team_alilm.product.controller.dto.response.ProductCountResponse
import org.team_alilm.product.controller.dto.response.ProductDetailResponse
import org.team_alilm.product.controller.dto.response.ProductListResponse
import org.team_alilm.product.repository.ProductQueryRepository
import org.team_alilm.product.repository.ProductRepository

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val productQueryRepository: ProductQueryRepository
) {

    fun getProductCount(): ProductCountResponse {
        return ProductCountResponse(productCount = productRepository.count())
    }

    fun getProductDetail(productId: Long) =
        productQueryRepository.findProductDetail(productId)
            ?.let {
                ProductDetailResponse(
                    id = it.id,
                    number = it.number,
                    name = it.name,
                    brand = it.brand,
                    thumbnailUrl = it.thumbnailUrl,
                    imageUrlList = it.imageUrlList,
                    store = it.store,
                    price = it.price,
                    firstOption = it.firstOption,
                    secondOption = it.secondOption,
                    thirdOption = it.thirdOption,
                    waitingCount = it.waitingCount
                )
            }
            ?: throw BusinessException(ErrorCode.PRODUCT_NOT_FOUND)

    fun getProductList(param : ProductListParam) : ProductListResponse =
        productQueryRepository.findProductsByCursor(param)
}


