package org.team_alilm.application.port.`in`.use_case

import org.team_alilm.domain.Member

interface MyAlilmCountUseCase {

    fun myAlilmCount(command: MyAlilmCountCommand): MyAlilmCountResult

    data class MyAlilmCountCommand(
        val member: Member
    )

    data class MyAlilmCountResult(
        val count: Int,
        val alilmCount: Int,
        val basketCount: Int
    )

}