package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.CopyBasketUseCase
import org.team_alilm.application.port.out.LoadBasketPort
import org.team_alilm.domain.Basket
import org.team_alilm.domain.Product
import org.team_alilm.global.error.ErrorMessage
import org.team_alilm.global.error.NotFoundProductException

@Service
@Transactional(readOnly = true)
class CopyBasketService(
    private val loadProductPort: org.team_alilm.application.port.out.LoadProductPort,
    private val addBasketPort: org.team_alilm.application.port.out.AddBasketPort,
    private val loadBasketPort: LoadBasketPort
) : CopyBasketUseCase {

    @Transactional
    override fun copyBasket(command: CopyBasketUseCase.CopyBasketCommand) {
        val productId = Product.ProductId(command.productId)

        val basket = Basket.create(
            productId = productId,
            memberId = command.member.id!!
        )

        // 상품 조회
        val product = loadProductPort.loadProduct(productId = productId)
            ?: throw NotFoundProductException(ErrorMessage.NOT_FOUND_PRODUCT)

        // 이미 장바구니에 담긴 상품인지 확인
        loadBasketPort.loadBasket(
            memberId = command.member.id,
            productId = productId
        )?.let { return }

        // 장바구니에 상품 추가
        addBasketPort.addBasket(
            basket = basket,
            product = product,
            member = command.member
        )
    }

}