package org.teamalilm.alilm.application.port.`in`.use_case

import org.teamalilm.alilm.domain.Member

interface AddFcmTokenUseCase {

    fun addFcmToken(command: AddFcmTokenCommand)

    data class AddFcmTokenCommand(
        val token: String,
        val member: Member
    )
}