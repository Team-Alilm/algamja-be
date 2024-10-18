package org.team_alilm.application.port.`in`.use_case

interface DeleteBasketUseCase {

    fun deleteBasket(command: DeleteBasketCommand)

    data class DeleteBasketCommand(
        val memberId: Long,
        val basketId: Long
    )

}