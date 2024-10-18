package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.DeleteBasketUseCase
import org.team_alilm.application.port.out.DeleteBasketPort

@Service
@Transactional(readOnly = true)
class DeleteBasketService(
    private val deleteBasketPort: DeleteBasketPort
) : DeleteBasketUseCase {

    @Transactional
    override fun deleteBasket(command: DeleteBasketUseCase.DeleteBasketCommand) {
        deleteBasketPort.deleteBasket(command.memberId, command.basketId)
    }

}
