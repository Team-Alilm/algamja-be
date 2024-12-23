package org.team_alilm.application.port.out

import org.team_alilm.domain.product.Product

interface LoadAlilmPort {

    fun loadTop7Alilm() : List<Product>
}