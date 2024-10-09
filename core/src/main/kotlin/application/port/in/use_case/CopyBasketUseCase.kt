package org.team_alilm.application.port.`in`.use_case

import org.teamalilm.alilm.adapter.out.security.CustomMemberDetails

interface CopyBasketUseCase {

    fun copyBasket(command: org.team_alilm.application.port.`in`.use_case.CopyBasketUseCase.CopyBasketCommand)

    data class CopyBasketCommand(
        val productId: Long,
        val customMemberDetails: CustomMemberDetails
    )
}