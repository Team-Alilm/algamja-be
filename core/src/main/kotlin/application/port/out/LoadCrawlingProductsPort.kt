package org.team_alilm.application.port.out

import org.team_alilm.adapter.out.persistence.repository.product.ProductAndMembersList

interface LoadCrawlingProductsPort {

    fun loadCrawlingProducts(): List<ProductAndMembersList>

}