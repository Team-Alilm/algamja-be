package org.team_alilm.application.port.`in`.use_case

import domain.product.ProductId

interface BasketAlilmUseCase {

    fun basketAlilm(command: BasketAlilmCommand)

    data class BasketAlilmCommand(
        val productId: Long,
    )
}