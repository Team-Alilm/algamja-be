package org.teamalilm.alilmbe.service.basket

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.adapter.`in`.web.controller.BasketMyController
import org.teamalilm.alilmbe.adapter.out.persistence.repository.BasketRepository
import org.teamalilm.alilmbe.adapter.out.persistence.entity.member.Member
import org.teamalilm.alilmbe.domain.product.entity.Product.*

@Service
@Transactional(readOnly = true)
class BasketMyService (
    private val basketRepository: BasketRepository
) {

    fun myBasket(command: MyBasketCommand): List<BasketMyController.BasketMyResponse> {
        return basketRepository.findAllByMember(command.member).map {
            BasketMyController.BasketMyResponse(
                id = it.product.id!!,
                name = it.product.name,
                brand = it.product.brand,
                imageUrl = it.product.imageUrl,
                category = it.product.category,
                price = it.product.price,
                productInfo = ProductInfo(
                    store = it.product.productInfo.store,
                    number = it.product.productInfo.number,
                    option1 = it.product.productInfo.option1,
                    option2 = it.product.productInfo.option2,
                    option3 = it.product.productInfo.option3
                ),
                createdDate = it.createdDate
            )
        }
    }

    data class MyBasketCommand(
        val member: Member
    )
}