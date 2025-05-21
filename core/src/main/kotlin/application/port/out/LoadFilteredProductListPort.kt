package org.team_alilm.application.port.out

import org.team_alilm.application.port.`in`.use_case.ProductSliceUseCase

interface LoadFilteredProductListPort {

    fun getFilteredProductList(
        size: Int,
        category: String?,
        sort: String,
        price: Int?,
        productId: Long?,
        waitingCount: Long?,
    ): ProductSliceUseCase.CustomSlice

    fun getFilteredProductListV2(
        size: Int,
        page: Int,
        category: String?,
    ): ProductSliceUseCase.CustomSlice

}