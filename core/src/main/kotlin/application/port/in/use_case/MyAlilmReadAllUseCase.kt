package org.team_alilm.application.port.`in`.use_case

import domain.Member

interface MyAlilmReadAllUseCase {

    fun myAlilmReadAll(command: MyAlilmReadCommand)

    data class MyAlilmReadCommand(
        val member: Member
    )
}