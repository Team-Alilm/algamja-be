package org.team_alilm.application.port.out

import domain.product.Product

interface LoadAlilmPort {

    fun loadAlilm(count: Int) : List<Product>
}