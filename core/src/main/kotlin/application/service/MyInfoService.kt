package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.MyInfoUseCase
import org.team_alilm.global.error.NotFoundMemberException

@Service
@Transactional
class MyInfoService(
    val loadMemberPort: org.team_alilm.application.port.out.LoadMemberPort
) : MyInfoUseCase {

    override fun myInfo(command: org.team_alilm.application.port.`in`.use_case.MyInfoUseCase.MyInfoCommand): org.team_alilm.application.port.`in`.use_case.MyInfoUseCase.MyInfoResult {
        val member = loadMemberPort.loadMember(command.member.id!!.value)
            ?: throw NotFoundMemberException()

        return org.team_alilm.application.port.`in`.use_case.MyInfoUseCase.MyInfoResult(
            nickname = member.nickname,
            email = member.email
        )
    }

}