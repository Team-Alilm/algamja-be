package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.AlilmRecentUseCase
import org.team_alilm.application.port.out.LoadProductPort
import org.team_alilm.domain.Product

@Service
@Transactional(readOnly = true)
class AlilmRecentService(
    private val loadProductPort: LoadProductPort
) : AlilmRecentUseCase {

    override fun alilmRecent(): AlilmRecentUseCase.AlilmRecentResult {
        val productList = loadProductPort.loadRecentProduct()

        return AlilmRecentUseCase.AlilmRecentResult(
            productList = productList
        )
    }

    data class AlilmRecentResult(
        val productList : List<Product>
    )
}