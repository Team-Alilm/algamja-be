package org.teamalilm.alilm.application.port.out

import org.teamalilm.alilm.domain.Product

interface AddProductPort {

    fun addProduct(product: Product): Product

}

