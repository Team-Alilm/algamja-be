package org.teamalilm.alilm.application.port.`in`.use_case

import org.teamalilm.alilm.domain.Member

interface AddFcmTokenUseCase {

    fun addFcmToken(command: AddFcmTokenCommand)

    data class AddFcmTokenCommand(
        val fcmToken: String,
        val member: Member
    )
}