package org.teamalilm.alilmbe.application.port.out.gateway

interface SendSlackGateway {

    fun sendMessage(
        message: String
    )

}