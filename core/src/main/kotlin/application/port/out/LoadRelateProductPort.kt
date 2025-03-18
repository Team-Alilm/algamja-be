package org.team_alilm.application.port.out

import domain.product.Product

interface LoadRelateProductPort {

    fun loadRelateProduct(firstCategory: String, secondCategory: String?): List<Product>
}