package org.team_alilm.application.port.`in`.use_case

interface AlilmHistoryUseCase {

    fun alilmHistory(): org.team_alilm.application.port.`in`.use_case.AlilmHistoryUseCase.AlilmHistoryResult

    data class AlilmHistoryResult(
        val allCount : Long,
        val dailyCount : Long,
    )
}