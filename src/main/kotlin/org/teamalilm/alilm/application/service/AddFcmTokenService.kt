package org.teamalilm.alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilm.application.port.`in`.use_case.AddFcmTokenUseCase
import org.teamalilm.alilm.application.port.out.AddMemberPort

@Service
@Transactional(readOnly = true)
class AddFcmTokenService(
    private val addMemberPort: AddMemberPort
) : AddFcmTokenUseCase {

    @Transactional
    override fun addFcmToken(command: AddFcmTokenUseCase.AddFcmTokenCommand) {
        val member = command.member
        val fcmToken = command.fcmToken

        member.fcmToken = fcmToken

        addMemberPort.addMember(member)
    }

}