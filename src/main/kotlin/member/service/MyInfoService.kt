package org.team_alilm.member.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.MyInfoUseCase
import org.team_alilm.application.port.out.AddMemberPort

@Service
@Transactional(readOnly = true)
class MyInfoService(
    private val addMemberPort: AddMemberPort
) : MyInfoUseCase {

    override fun myInfo(command: MyInfoUseCase.MyInfoCommand): MyInfoUseCase.MyInfoResult {
        return MyInfoUseCase.MyInfoResult(
            nickname = command.member.nickname,
            email = command.member.email
        )
    }

    @Transactional
    override fun updateMyInfo(command: MyInfoUseCase.UpdateMyInfoCommand) {
        val member = command.member
        member.changeInfo(
            newNickname = command.nickname,
            newEmail = command.email
        )

        addMemberPort.addMember(member)
    }

}