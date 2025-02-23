package org.team_alilm.application.port.`in`.use_case

import domain.Member

interface MyAlilmHistoryCountUseCase {

    fun myAlilmHistoryCount(command: MyAlilmHistoryCountCommand): MyAlilmHistoryCountResult

    data class MyAlilmHistoryCountCommand(
        val member: Member
    )

    data class MyAlilmHistoryCountResult(
        val readYCount: Long
    )
}