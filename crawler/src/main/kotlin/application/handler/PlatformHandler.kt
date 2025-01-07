package org.team_alilm.application.handler

import domain.product.Product

interface PlatformHandler {

    fun process(product: Product): Boolean
}