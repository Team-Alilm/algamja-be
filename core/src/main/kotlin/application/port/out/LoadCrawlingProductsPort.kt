package org.team_alilm.application.port.out

import domain.product.Product

interface LoadCrawlingProductsPort {

    fun loadCrawlingProducts(): List<Product>

}