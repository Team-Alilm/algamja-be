package org.teamalilm.alilm.application.port.out

import org.teamalilm.alilm.domain.Product

interface LoadProductPort {

    fun loadProduct(
        number:Long,
        store: Product.Store,
        option1: String,
        option2: String?,
        option3: String?
    ): Product?

}