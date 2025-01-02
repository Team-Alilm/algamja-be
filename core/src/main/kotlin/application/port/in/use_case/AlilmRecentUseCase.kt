package org.team_alilm.application.port.`in`.use_case

import domain.product.Product

interface AlilmRecentUseCase {

    fun alilmRecent(): AlilmRecentResult

    data class AlilmRecentResult(
        val productList : List<Product>
    )

}