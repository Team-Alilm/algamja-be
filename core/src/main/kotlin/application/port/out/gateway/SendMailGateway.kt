package org.team_alilm.application.port.out.gateway

import domain.Member
import domain.product.Product

interface SendMailGateway {

    fun sendMail (
        member: Member,
        product: Product
    )

}