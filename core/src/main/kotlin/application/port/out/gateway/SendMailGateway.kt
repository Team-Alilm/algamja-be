package org.team_alilm.application.port.out.gateway

interface SendMailGateway {

    fun sendMail (
        message: String,
        to: String
    )

}