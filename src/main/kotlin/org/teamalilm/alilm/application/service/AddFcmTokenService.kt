package org.teamalilm.alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilm.application.port.`in`.use_case.AddFcmTokenUseCase
import org.teamalilm.alilm.application.port.out.AddFcmTokenPort
import org.teamalilm.alilm.application.port.out.AddMemberPort
import org.teamalilm.alilm.domain.FcmToken

@Service
@Transactional(readOnly = true)
class AddFcmTokenService(
    private val addFcmTokenPort: AddFcmTokenPort
) : AddFcmTokenUseCase {

    @Transactional
    override fun addFcmToken(command: AddFcmTokenUseCase.AddFcmTokenCommand) {
        val member = command.member
        val token = command.token

        val fcmToken = FcmToken(token, member)

        addFcmTokenPort.addFcmToken(fcmToken)
    }

}