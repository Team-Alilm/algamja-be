package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.AddFcmTokenUseCase
import org.team_alilm.domain.FcmToken

@Service
@Transactional(readOnly = true)
class AddFcmTokenService(
    private val addFcmTokenPort: org.team_alilm.application.port.out.AddFcmTokenPort
) : AddFcmTokenUseCase {

    @Transactional
    override fun addFcmToken(command: AddFcmTokenUseCase.AddFcmTokenCommand) {
        val member = command.member
        val token = command.token
        val fcmToken = FcmToken(token, member.id!!)

        addFcmTokenPort.addFcmToken(fcmToken)
    }

}