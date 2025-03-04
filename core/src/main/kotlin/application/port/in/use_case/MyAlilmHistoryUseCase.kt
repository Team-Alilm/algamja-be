package org.team_alilm.application.port.`in`.use_case

import domain.Member

interface MyAlilmHistoryUseCase {

    fun myAlilmHistory(command: MyAlilmHistoryCommand): List<MyAlilmHistoryResult>

    data class MyAlilmHistoryCommand(
        val member: Member
    )

    data class MyAlilmHistoryResult(
        val alilmId: Long,
        val productid: Long,
        val productId: Long,
        val name: String,
        val imageUrl: String,
        val brand: String,
        val price: Int,
        val firstOption: String?,
        val secondOption: String?,
        val thirdOption: String?,
        val readYn: Boolean
    )
}