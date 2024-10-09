package org.team_alilm.application.port.out.gateway

interface SendSlackGateway {

    fun sendMessage(
        message: String
    )

}