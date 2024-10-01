package org.teamalilm.alilm.application.port.out

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.teamalilm.alilm.domain.Product

interface LoadSliceBasketPort {

    fun loadBasketSlice(pageRequest: PageRequest): Slice<BasketAndCountProjection>

    data class BasketAndCountProjection(
        val product: Product,
        val waitingCount: Long
    )

}
