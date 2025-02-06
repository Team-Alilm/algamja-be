package org.team_alilm.application.port.out.gateway

import domain.product.Product

interface SendMailGateway {

    fun sendMail (
        to: String,
        nickname: String,
        product: Product
    )

}