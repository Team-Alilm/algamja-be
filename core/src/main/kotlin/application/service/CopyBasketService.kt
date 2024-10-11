package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.domain.Basket
import org.team_alilm.domain.Product
import org.team_alilm.global.error.ErrorMessage
import org.team_alilm.global.error.NotFoundProductException

@Service
@Transactional(readOnly = true)
class CopyBasketService(
    private val loadProductPort: org.team_alilm.application.port.out.LoadProductPort,
    private val addBasketPort: org.team_alilm.application.port.out.AddBasketPort
) : org.team_alilm.application.port.`in`.use_case.CopyBasketUseCase {

    @Transactional
    override fun copyBasket(command: org.team_alilm.application.port.`in`.use_case.CopyBasketUseCase.CopyBasketCommand) {
        val productId = Product.ProductId(command.productId)

        val basket = Basket.create(
            productId = productId,
            memberId = command.customMemberDetails.member.id!!
        )

        val product = loadProductPort.loadProduct(productId = productId)
            ?: throw NotFoundProductException(ErrorMessage.NOT_FOUND_PRODUCT)

        addBasketPort.addBasket(
            basket = basket,
            product = product,
            member = command.customMemberDetails.member
        )
    }

}