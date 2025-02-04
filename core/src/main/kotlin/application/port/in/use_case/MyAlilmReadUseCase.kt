package org.team_alilm.application.port.`in`.use_case

import domain.Member

interface MyAlilmReadUseCase {

    fun myAlilmRead(command: MyAlilmReadCommand)

    data class MyAlilmReadCommand(
        val member: Member,
        val alilmIdList: List<Long>
    )
}