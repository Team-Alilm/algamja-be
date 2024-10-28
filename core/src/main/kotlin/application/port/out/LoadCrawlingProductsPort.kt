package org.team_alilm.application.port.out

import org.team_alilm.domain.Product

interface LoadCrawlingProductsPort {

    fun loadCrawlingProducts(): List<Product>
}