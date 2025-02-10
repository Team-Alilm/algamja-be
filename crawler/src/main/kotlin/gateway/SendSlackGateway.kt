package org.team_alilm.gateway

import domain.product.Product

interface SendSlackGateway {

    fun sendMessage(
        message: String
    )

    fun sendMessage(
        product: Product
    )

}