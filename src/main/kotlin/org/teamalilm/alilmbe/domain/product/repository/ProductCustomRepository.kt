package org.teamalilm.alilmbe.domain.product.repository

import org.springframework.data.domain.Slice
import org.teamalilm.alilmbe.domain.product.repository.impl.ProductCustomRepositoryImpl
import org.teamalilm.alilmbe.domain.product.repository.impl.ProductCustomRepositoryImpl.ProductListQuery

interface ProductCustomRepository {

    fun productList(query: ProductListQuery): Slice<ProductCustomRepositoryImpl.ProductListProjection>

}