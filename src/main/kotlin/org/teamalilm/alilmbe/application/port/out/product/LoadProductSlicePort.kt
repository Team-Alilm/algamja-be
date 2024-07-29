package org.teamalilm.alilmbe.application.port.out.product

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.teamalilm.alilmbe.adapter.out.persistence.adapter.BasketPersistenceAdapter.BasketCountData

interface LoadBasketSlicePort {

    fun loadBasketSlice(pageRequest: PageRequest): Slice<BasketCountData>

}
