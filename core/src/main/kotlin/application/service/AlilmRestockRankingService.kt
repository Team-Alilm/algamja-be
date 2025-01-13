package org.team_alilm.application.service

import domain.product.Product
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.AlilmRestockRankingUseCase
import org.team_alilm.application.port.out.LoadAlilmPort

@Service
@Transactional(readOnly = true)
class AlilmRestockRankingService(
    private val loadAlilmPort: LoadAlilmPort
): AlilmRestockRankingUseCase {

    override fun alilmRestockRangin(
        command: AlilmRestockRankingUseCase.AlilmRestockRankingCommand
    ) : List<Product> {
        return loadAlilmPort.loadAlilm(command.count)
    }
}