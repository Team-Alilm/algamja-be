package org.team_alilm.application.port.out

import org.team_alilm.domain.product.Product

interface LoadCrawlingProductsPort {

    fun loadCrawlingProducts(): List<Product>

}