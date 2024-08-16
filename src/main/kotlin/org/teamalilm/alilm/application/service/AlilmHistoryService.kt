package org.teamalilm.alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilm.application.port.`in`.use_case.AlilmHistoryUseCase
import org.teamalilm.alilm.application.port.out.LoadAllAndDailyCountPort

@Service
@Transactional(readOnly = true)
class AlilmHistoryService(
    private val loadAllAndDailyCountPort: LoadAllAndDailyCountPort
) : AlilmHistoryUseCase {

    override fun alilmHistory(): AlilmHistoryUseCase.AlilmHistoryResult {
        val counts = loadAllAndDailyCountPort.getAllAndDailyCount()

        return AlilmHistoryUseCase.AlilmHistoryResult(
            allCount = counts.allCount,
            dailyCount = counts.dailyCount
        )
    }

}