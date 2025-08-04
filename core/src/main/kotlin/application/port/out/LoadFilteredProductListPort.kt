package org.team_alilm.application.port.out

import org.team_alilm.application.port.`in`.use_case.ProductSliceUseCase
import org.team_alilm.domain.product.ProductCategory
import org.team_alilm.domain.product.ProductSortType

interface LoadFilteredProductListPort {

    fun getFilteredProductListV3(
        size: Int,
        category: ProductCategory?,
        sort: ProductSortType,
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