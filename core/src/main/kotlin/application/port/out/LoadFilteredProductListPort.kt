package org.team_alilm.application.port.out

import org.springframework.data.domain.Page
import org.springframework.data.domain.Slice
import org.team_alilm.adapter.out.persistence.repository.product.ProductAndWaitingCount
import org.team_alilm.application.port.`in`.use_case.ProductSliceUseCase

interface LoadFilteredProductListPort {

    fun getFilteredProductList(
        category: String?,
        size: Int,
        sort: String,
        sortKey: String
    ): ProductSliceUseCase.CustomSlice

}