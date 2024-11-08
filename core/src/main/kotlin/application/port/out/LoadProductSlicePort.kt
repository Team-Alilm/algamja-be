package org.team_alilm.application.port.out

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.team_alilm.domain.product.Product

interface LoadProductSlicePort {

    fun loadProductSlice(pageRequest: PageRequest): Slice<ProductAndWaitingCount>

    data class ProductAndWaitingCount(
        val product: Product,
        val waitingCount: Long,
    ) {

        companion object {
            fun of (product: Product, waitingCount: Long): ProductAndWaitingCount {
                return ProductAndWaitingCount(
                    product = product,
                    waitingCount = waitingCount,
                )
            }
        }
    }
}
