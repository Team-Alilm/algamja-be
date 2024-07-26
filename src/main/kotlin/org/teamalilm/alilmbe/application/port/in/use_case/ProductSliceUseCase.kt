package org.teamalilm.alilmbe.application.port.`in`.use_case

import org.springframework.data.domain.Slice

interface BasketSliceUseCase {

    fun basketSlice(command: BasketListCommand): Slice<BasketListResult>

    data class BasketListCommand(
        val page: Int,
        val size: Int
    )

    data class BasketListResult(
        val id: Long,
        val number: Long,
        val name: String,
        val brand: String,
        val imageUrl: String,
        val price: Int,
        val category: String,
        val option1: String,
        val option2: String?,
        val option3: String?
    )
}