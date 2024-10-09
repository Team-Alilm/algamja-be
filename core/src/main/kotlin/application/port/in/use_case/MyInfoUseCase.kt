package org.team_alilm.application.port.`in`.use_case

import org.teamalilm.alilm.domain.Member

interface MyInfoUseCase {

    fun myInfo(command: org.team_alilm.application.port.`in`.use_case.MyInfoUseCase.MyInfoCommand): org.team_alilm.application.port.`in`.use_case.MyInfoUseCase.MyInfoResult

    data class MyInfoCommand(
        val member: Member
    )

    data class MyInfoResult(
        val nickname: String,
        val email: String
    )
}