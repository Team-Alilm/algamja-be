package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.MyBasketsUseCase
import org.team_alilm.application.port.`in`.use_case.MyBasketsUseCase.*
import org.team_alilm.application.port.out.LoadBasketPort
import org.team_alilm.global.error.NotFoundProductException

@Service
@Transactional
class MyBasketsService (
    val loadMyBasketsPort: org.team_alilm.application.port.out.LoadMyBasketsPort,
    val loadBasketPort: LoadBasketPort
) : MyBasketsUseCase {

    override fun myBasket(command: MyBasketCommand): List<MyBasketsResult> {
        val myBasketAndProductList = loadMyBasketsPort.loadMyBaskets(command.member)

        return myBasketAndProductList.map {
            val waitingCount = loadBasketPort.loadBasketCount(it.product.id!!)

            MyBasketsResult(
                id = it.basket.id!!.value!!,
                number = it.product.number,
                name = it.product.name,
                brand = it.product.brand,
                imageUrl = it.product.thumbnailUrl,
                store = it.product.store.name,
                price = it.product.price,
                firstCategory = it.product.firstCategory,
                isAlilm = it.basket.isAlilm,
                alilmDate = it.basket.alilmDate,
                firstOption = it.product.firstOption,
                secondOption = it.product.secondOption,
                thirdOption = it.product.thirdOption,
                isHidden = it.basket.isHidden,
                waitingCount = waitingCount,
                productId = it.product.id ?: throw NotFoundProductException()
            )
        }
    }
}