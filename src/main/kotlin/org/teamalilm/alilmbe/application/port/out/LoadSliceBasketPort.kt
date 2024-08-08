package org.teamalilm.alilmbe.application.port.out

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.teamalilm.alilmbe.domain.Product

interface LoadSliceBasketPort {

    fun loadBasketSlice(pageRequest: PageRequest): Slice<BasketCountData>

    data class BasketCountData(
        val product: Product,
        val waitingCount: Long
    )

}
