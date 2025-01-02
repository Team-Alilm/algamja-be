package org.team_alilm.application.port.`in`.use_case

import domain.Member

interface MyAlilmCountUseCase {

    fun myAlilmCount(command: MyAlilmCountCommand): MyAlilmCountResult

    data class MyAlilmCountCommand(
        val member: Member
    )

    data class MyAlilmCountResult(
        val alilmCount: Int,
        val basketCount: Int
    )

}