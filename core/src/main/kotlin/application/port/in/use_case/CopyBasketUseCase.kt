package org.team_alilm.application.port.`in`.use_case

import org.team_alilm.domain.Member


interface CopyBasketUseCase {

    fun copyBasket(command: CopyBasketCommand)

    data class CopyBasketCommand(
        val productId: Long,
        val member: Member
    )
}