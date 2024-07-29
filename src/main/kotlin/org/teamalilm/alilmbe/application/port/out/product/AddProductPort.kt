package org.teamalilm.alilmbe.application.port.out.product

import org.teamalilm.alilmbe.domain.product.Product

interface AddProductPort {

    fun addProduct(product: Product): Product

}

