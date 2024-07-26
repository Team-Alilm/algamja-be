package org.teamalilm.alilmbe.application.port.out.product

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.ProductJpaEntity

interface LoadBasketSlicePort {

    fun loadBasketSlice(pageRequest: PageRequest): Slice<ProductBasketCountProjection>



}

data class ProductBasketCountProjection(
    val product: ProductJpaEntity,
    val count: Long
)