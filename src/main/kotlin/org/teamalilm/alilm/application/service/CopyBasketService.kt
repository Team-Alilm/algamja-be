package org.teamalilm.alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilm.application.port.`in`.use_case.CopyBasketUseCase
import org.teamalilm.alilm.application.port.out.AddBasketPort
import org.teamalilm.alilm.application.port.out.LoadProductPort
import org.teamalilm.alilm.common.error.ErrorMessage
import org.teamalilm.alilm.common.error.NotFoundProductException
import org.teamalilm.alilm.domain.Basket
import org.teamalilm.alilm.domain.Product

@Service
@Transactional(readOnly = true)
class CopyBasketService(
    private val loadProductPort: LoadProductPort,
    private val addBasketPort: AddBasketPort
) : CopyBasketUseCase {

    @Transactional
    override fun copyBasket(command: CopyBasketUseCase.CopyBasketCommand) {
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