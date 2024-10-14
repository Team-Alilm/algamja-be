package org.team_alilm.application.port.`in`.use_case

import org.team_alilm.domain.Member

interface MyInfoUseCase {

    fun myInfo(command: MyInfoCommand): MyInfoResult

    data class MyInfoCommand(
        val member: Member
    )

    data class MyInfoResult(
        val nickname: String,
        val email: String
    )
}