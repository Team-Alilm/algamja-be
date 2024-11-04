package org.team_alilm.application.port.`in`.use_case

interface AlilmHistoryUseCase {

    fun alilmHistory(): AlilmHistoryResult

    data class AlilmHistoryResult(
        val allCount : Long,
        val dailyCount : Long,
    )
}