package org.teamalilm.alilmbe.application.port.out

import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.Product

interface AddProductPort{

    fun addProduct(product: Product): Product
}