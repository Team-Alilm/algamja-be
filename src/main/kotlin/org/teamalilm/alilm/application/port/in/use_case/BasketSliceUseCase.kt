package org.teamalilm.alilm.application.port.`in`.use_case

import org.springframework.data.domain.Slice
import org.teamalilm.alilm.adapter.`in`.web.controller.baskets.BasketSliceController

interface BasketSliceUseCase {

    fun basketSlice(command: BasketListCommand): Slice<BasketListResult>

    data class BasketListCommand(
        val page: Int,
        val size: Int
    ) {

        companion object {
            fun from(productListParameter: BasketSliceController.ProductListParameter): BasketListCommand {
                return BasketListCommand(
                    page = productListParameter.page,
                    size = productListParameter.size
                )
            }
        }
    }

    data class BasketListResult(
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
        val waitingCount: Long
    )
}