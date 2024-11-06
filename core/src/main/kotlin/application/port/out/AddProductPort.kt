package org.team_alilm.application.port.out

import org.team_alilm.domain.product.Product
import org.team_alilm.domain.product.ProductV2

interface AddProductPort {

    fun addProduct(product: Product): Product

    fun addProduct(product: ProductV2) : ProductV2
}

