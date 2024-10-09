package org.team_alilm.application.port.`in`.use_case

import org.teamalilm.alilm.domain.Member

interface MyAlilmCountUseCase {

    fun myAlilmCount(command: org.team_alilm.application.port.`in`.use_case.MyAlilmCountUseCase.MyAlilmCountCommand): org.team_alilm.application.port.`in`.use_case.MyAlilmCountUseCase.MyAlilmCountResult

    data class MyAlilmCountCommand(
        val member: Member
    )

    data class MyAlilmCountResult(
        val count: Int
    )

}