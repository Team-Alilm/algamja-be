package org.teamalilm.alilmbe.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.application.port.`in`.use_case.MyBasketsUseCase
import org.teamalilm.alilmbe.application.port.out.LoadMyBasketsPort

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
                option1 = it.product.option1,
                option2 = it.product.option2,
                option3 = it.product.option3,
                isHidden = it.basket.isHidden
            )
        }
    }
}