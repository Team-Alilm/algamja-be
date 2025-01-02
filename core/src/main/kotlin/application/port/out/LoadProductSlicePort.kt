package org.team_alilm.application.port.out

import domain.product.Product
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice

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
