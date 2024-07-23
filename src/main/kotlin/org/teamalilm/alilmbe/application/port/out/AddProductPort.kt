package org.teamalilm.alilmbe.application.port.out

import org.teamalilm.alilmbe.domain.product.Product

interface AddProductPort {

    fun addProduct(product: Product)
}