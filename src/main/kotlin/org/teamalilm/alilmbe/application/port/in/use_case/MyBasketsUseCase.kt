package org.teamalilm.alilmbe.application.port.`in`.use_case

import org.teamalilm.alilmbe.domain.Basket
import org.teamalilm.alilmbe.domain.Member
import org.teamalilm.alilmbe.domain.Product

interface MyBasketsUseCase {

    fun myBasket(command: MyBasketCommand) : List<MyBasketsResult>

    data class MyBasketCommand(
        val member: Member
    )

    data class MyBasketsResult(
        val id: Long,
        val number: Long,
        val name: String,
        val brand: String,
        val imageUrl: String,
        val store: String,
        val price: Int,
        val category: String,
        val option1: String,
        val option2: String?,
        val option3: String?,
        val isHidden: Boolean
    ) {

        companion object {
            fun from(basket: Basket, product: Product) : MyBasketsResult {
                return MyBasketsResult(
                    id = basket.id?.value!!,
                    number = product.number,
                    name = product.name,
                    brand = product.brand,
                    imageUrl = product.imageUrl,
                    store = product.store.name,
                    price = product.price,
                    category = product.category,
                    option1 = product.option1,
                    option2 = product.option2,
                    option3 = product.option3,
                    isHidden = basket.isHidden
                )
            }
        }

    }

}