package org.teamalilm.alilmbe.application.port.out.product

import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.Store
import org.teamalilm.alilmbe.domain.product.Product

interface LoadProductPort {

    fun loadProduct(
        number:Long,
        store: Store,
        option1: String,
        option2: String?,
        option3: String?
    ): Product?
}