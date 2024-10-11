package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AlilmHistoryService(
    private val loadAllAndDailyCountPort: org.team_alilm.application.port.out.LoadAllAndDailyCountPort
) : org.team_alilm.application.port.`in`.use_case.AlilmHistoryUseCase {

    override fun alilmHistory(): org.team_alilm.application.port.`in`.use_case.AlilmHistoryUseCase.AlilmHistoryResult {
        val counts = loadAllAndDailyCountPort.getAllAndDailyCount()

        return org.team_alilm.application.port.`in`.use_case.AlilmHistoryUseCase.AlilmHistoryResult(
            allCount = counts.allCount,
            dailyCount = counts.dailyCount
        )
    }

}