package org.team_alilm.application.port.`in`.use_case


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
    }
}



