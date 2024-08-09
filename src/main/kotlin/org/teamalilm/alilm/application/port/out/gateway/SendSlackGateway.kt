package org.teamalilm.alilm.application.port.out.gateway

interface SendSlackGateway {

    fun sendMessage(
        message: String
    )

}