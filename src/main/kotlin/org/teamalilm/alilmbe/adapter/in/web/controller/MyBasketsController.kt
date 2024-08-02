package org.teamalilm.alilmbe.adapter.`in`.web.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.teamalilm.alilmbe.adapter.out.security.CustomMemberDetails
import org.teamalilm.alilmbe.application.port.`in`.use_case.MyBasketsUseCase

@RestController
@RequestMapping("/api/v1/baskets")
class MyBasketsController(
    private val myBasketsUseCase: MyBasketsUseCase
) {

    @GetMapping()
    fun myBasket(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ) : ResponseEntity<List<MyBasketsResponse>> {
        return ResponseEntity.ok(
            myBasketsUseCase.myBasket(
                MyBasketsUseCase.MyBasketCommand(customMemberDetails.member)
            ).map { MyBasketsResponse.from(it) }
        )

    }

    data class MyBasketsResponse(
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
            fun from(myBasketsResult: MyBasketsUseCase.MyBasketsResult) : MyBasketsResponse {
                return MyBasketsResponse(
                    id = myBasketsResult.id,
                    number = myBasketsResult.number,
                    name = myBasketsResult.name,
                    brand = myBasketsResult.brand,
                    imageUrl = myBasketsResult.imageUrl,
                    store = myBasketsResult.store,
                    price = myBasketsResult.price,
                    category = myBasketsResult.category,
                    option1 = myBasketsResult.option1,
                    option2 = myBasketsResult.option2,
                    option3 = myBasketsResult.option3,
                    isHidden = myBasketsResult.isHidden
                )
            }
        }

    }

}