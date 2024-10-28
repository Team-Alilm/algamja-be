package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.ProductDetailsUseCase
import org.team_alilm.application.port.out.LoadProductPort
import org.team_alilm.domain.Product
import org.team_alilm.global.error.NotFoundProductException

@Service
@Transactional(readOnly = true)
class ProductDetailsService(
    private val loadProductPort: LoadProductPort
) : ProductDetailsUseCase {

    override fun productDetails(command: ProductDetailsUseCase.ProductDetailsCommand): ProductDetailsUseCase.ProductDetailsResponse {
        val productAndWaitingCount = loadProductPort.loadProductDetails(Product.ProductId(command.productId)) ?: throw NotFoundProductException()

        return ProductDetailsUseCase.ProductDetailsResponse.from(
            product = productAndWaitingCount.product,
            waitingCount = productAndWaitingCount.waitingCount
        )
    }
}