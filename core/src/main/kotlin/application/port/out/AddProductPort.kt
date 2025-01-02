package org.team_alilm.application.port.out

import domain.product.Product

interface AddProductPort {

    fun addProduct(product: Product): Product
}

