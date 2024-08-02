package org.teamalilm.alilmbe.application.port.out.gateway

interface SendMailGateway {

    fun sendMail (
        message: String,
        to: String
    )

}