package org.team_alilm.application.port.`in`.use_case

import org.team_alilm.domain.Member

interface AddFcmTokenUseCase {

    fun addFcmToken(command: AddFcmTokenCommand)

    data class AddFcmTokenCommand(
        val token: String,
        val member: Member
    )
}