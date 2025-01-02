package org.team_alilm.application.port.out.gateway

import domain.product.Product

interface SendSlackGateway {

    fun sendMessage(
        message: String
    )

    fun sendMessage(
        product: Product
    )

}