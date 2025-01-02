package org.team_alilm.application.port.`in`.use_case

import domain.product.Product

interface AlilmRestockRankingUseCase {

    fun alilmRestockRangin(command: AlilmRestockRankingCommand): List<Product>

    data class AlilmRestockRankingCommand(
        val count: Int
    )
}