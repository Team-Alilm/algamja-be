package org.teamalilm.alilmbe.service.basket

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.adapter.`in`.web.controller.BasketMyController
import org.teamalilm.alilmbe.adapter.out.persistence.repository.product.BasketRepository
import org.teamalilm.alilmbe.adapter.out.persistence.entity.member.Member
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.ProductInfo

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
                    store = it.product.store,
                    number = it.product.number,
                    option1 = it.product.option1,
                    option2 = it.product.option2,
                    option3 = it.product.option3
                ),
                createdDate = it.createdDate
            )
        }
    }

    data class MyBasketCommand(
        val member: Member
    )
}