package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.ProductDetailsUseCase
import org.team_alilm.application.port.`in`.use_case.ProductDetailsUseCase.*
import org.team_alilm.application.port.out.LoadProductPort
import org.team_alilm.domain.product.ProductId
import org.team_alilm.global.error.NotFoundProductException

@Service
@Transactional(readOnly = true)
class ProductDetailsService(
    private val loadProductPort: LoadProductPort
) : ProductDetailsUseCase {

    override fun productDetails(command: ProductDetailsCommand): ProductDetailsResult {
        val productAndWaitingCount = loadProductPort.loadProductDetails(ProductId(command.productId)) ?: throw NotFoundProductException()

        return ProductDetailsResult.from(
            product = productAndWaitingCount.product,
            waitingCount = productAndWaitingCount.waitingCount,
            imageUrlList = productAndWaitingCount.imageUrlList
        )
    }
}