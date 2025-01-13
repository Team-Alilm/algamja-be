package org.team_alilm.application.port.`in`.use_case

import domain.Member

interface MyInfoUseCase {

    fun myInfo(command: MyInfoCommand): MyInfoResult

    data class MyInfoCommand(
        val member: Member
    )

    data class MyInfoResult(
        val nickname: String,
        val email: String
    )

    fun updateMyInfo(command: UpdateMyInfoCommand)

    data class UpdateMyInfoCommand(
        val member: Member,
        val email: String,
        val nickname: String
    )
}