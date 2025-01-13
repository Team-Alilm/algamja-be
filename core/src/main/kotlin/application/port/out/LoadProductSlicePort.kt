package org.team_alilm.application.port.out

import domain.product.Product
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice

interface LoadProductSlicePort {

    fun loadProductSlice(pageRequest: PageRequest): Slice<Product>
}
