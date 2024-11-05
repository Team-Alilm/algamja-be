package org.team_alilm.application.port.out

import org.team_alilm.domain.Product
import org.team_alilm.domain.ProductV2

interface AddProductPort {

    fun addProduct(product: Product): Product

    fun addProduct(product: ProductV2) : ProductV2
}

