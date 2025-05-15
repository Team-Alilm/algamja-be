package org.team_alilm.application.port.out

import org.springframework.data.domain.Page
import org.team_alilm.adapter.out.persistence.repository.product.ProductAndWaitingCount

interface LoadFilteredProductListPort {

    fun getFilteredProductList(
        category: String?,
        size: Int,
        page: Int,
        sort: String
    ): List<ProductAndWaitingCount>

}