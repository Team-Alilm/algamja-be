package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MyBasketsService (
    val loadMyBasketsPort: org.team_alilm.application.port.out.LoadMyBasketsPort
) : org.team_alilm.application.port.`in`.use_case.MyBasketsUseCase {

    override fun myBasket(command: org.team_alilm.application.port.`in`.use_case.MyBasketsUseCase.MyBasketCommand): List<org.team_alilm.application.port.`in`.use_case.MyBasketsUseCase.MyBasketsResult> {
        val myBasketAndProductList = loadMyBasketsPort.loadMyBaskets(command.member)

        return myBasketAndProductList.map {
            org.team_alilm.application.port.`in`.use_case.MyBasketsUseCase.MyBasketsResult(
                id = it.basket.id!!.value!!,
                number = it.product.number,
                name = it.product.name,
                brand = it.product.brand,
                imageUrl = it.product.imageUrl,
                store = it.product.store.name,
                price = it.product.price,
                category = it.product.category,
                firstOption = it.product.firstOption,
                secondOption = it.product.secondOption,
                thirdOption = it.product.thirdOption,
                isHidden = it.basket.isHidden
            )
        }
    }
}