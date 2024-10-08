package org.teamalilm.alilm.application.port.`in`.use_case

import org.teamalilm.alilm.adapter.`in`.web.controller.products.PriceHistoryController.PriceHistoryRequest

interface PriceHistoryUseCase {

    fun priceHistory(command: PriceHistoryCommand): PriceHistoryResult

    data class PriceHistoryCommand(
        val productId: Long
    )

    data class PriceHistoryResult(
        val priceHistoryList: List<PriceHistory>
    ) {
        data class PriceHistory(
            val price: Int,
            val date: String
        )

        companion object {
            fun from(request: PriceHistoryRequest): PriceHistoryCommand {
                return PriceHistoryCommand(productId = request.productId)
            }
        }
    }
}



