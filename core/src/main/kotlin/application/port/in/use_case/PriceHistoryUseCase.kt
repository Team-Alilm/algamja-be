package org.team_alilm.application.port.`in`.use_case

import org.teamalilm.alilm.adapter.`in`.web.controller.products.PriceHistoryController.PriceHistoryRequest

interface PriceHistoryUseCase {

    fun priceHistory(command: org.team_alilm.application.port.`in`.use_case.PriceHistoryUseCase.PriceHistoryCommand): org.team_alilm.application.port.`in`.use_case.PriceHistoryUseCase.PriceHistoryResult

    data class PriceHistoryCommand(
        val productId: Long
    )

    data class PriceHistoryResult(
        val priceHistoryList: List<org.team_alilm.application.port.`in`.use_case.PriceHistoryUseCase.PriceHistoryResult.PriceHistory>
    ) {
        data class PriceHistory(
            val price: Int,
            val date: String
        )

        companion object {
            fun from(request: PriceHistoryRequest): org.team_alilm.application.port.`in`.use_case.PriceHistoryUseCase.PriceHistoryCommand {
                return org.team_alilm.application.port.`in`.use_case.PriceHistoryUseCase.PriceHistoryCommand(productId = request.productId)
            }
        }
    }
}



