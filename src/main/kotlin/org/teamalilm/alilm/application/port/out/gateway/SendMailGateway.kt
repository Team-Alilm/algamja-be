package org.teamalilm.alilm.application.port.out.gateway

interface SendMailGateway {

    fun sendMail (
        message: String,
        to: String
    )

}