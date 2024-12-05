package org.team_alilm.application.port.out.gateway

import org.team_alilm.domain.product.Product

interface SendMailGateway {

    fun sendMail (
        to: String,
        nickname: String,
        product: Product
    )

}