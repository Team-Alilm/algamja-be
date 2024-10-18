package org.team_alilm.application.port.out.gateway

interface SendMailGateway {

    fun sendMail (
        to: String, nickname: String, productNumber: Number, imageUrl: String, options: String
    )

}