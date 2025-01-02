package org.team_alilm.handler

import domain.product.Product

interface PlatformHandler {

    fun process(product: Product): Boolean
}