package org.teamalilm.alilm.application.port.`in`.use_case

import org.teamalilm.alilm.adapter.out.security.CustomMemberDetails

interface CopyBasketUseCase {

    fun copyBasket(command: CopyBasketCommand)

    data class CopyBasketCommand(
        val productId: Long,
        val customMemberDetails: CustomMemberDetails
    )
}