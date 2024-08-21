package org.teamalilm.alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilm.application.port.`in`.use_case.MyBasketsUseCase
import org.teamalilm.alilm.application.port.out.LoadMyBasketsPort

@Service
@Transactional
class MyBasketsService (
    val loadMyBasketsPort: LoadMyBasketsPort
) : MyBasketsUseCase {

    override fun myBasket(command: MyBasketsUseCase.MyBasketCommand): List<MyBasketsUseCase.MyBasketsResult> {
        val myBasketAndProductList = loadMyBasketsPort.loadMyBaskets(command.member)

        return myBasketAndProductList.map {
            MyBasketsUseCase.MyBasketsResult(
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