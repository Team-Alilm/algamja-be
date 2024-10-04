package org.teamalilm.alilm.application.port.`in`.use_case

import org.teamalilm.alilm.domain.Member

interface MyAlilmCountUseCase {

    fun myAlilmCount(command: MyAlilmCountCommand): MyAlilmCountResult

    data class MyAlilmCountCommand(
        val member: Member
    )

    data class MyAlilmCountResult(
        val count: Int
    )

}