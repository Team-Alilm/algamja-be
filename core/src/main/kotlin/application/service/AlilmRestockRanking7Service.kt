package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.AlilmRestockRanking7UseCase
import org.team_alilm.application.port.out.LoadAlilmPort
import org.team_alilm.domain.product.Product

@Service
@Transactional(readOnly = true)
class AlilmRestockRanking7Service(
    private val loadAlilmPort: LoadAlilmPort
): AlilmRestockRanking7UseCase {

    override fun alilmRestockRangin7() : List<Product> {
        return loadAlilmPort.loadTop7Alilm()
    }
}