package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.MyAlilmCountUseCase.MyAlilmCountResult

@Service
@Transactional(readOnly = true)
class MyAlilmCountService(
    private val loadBasketPort: org.team_alilm.application.port.out.LoadBasketPort
) : org.team_alilm.application.port.`in`.use_case.MyAlilmCountUseCase {

    override fun myAlilmCount(command: org.team_alilm.application.port.`in`.use_case.MyAlilmCountUseCase.MyAlilmCountCommand): MyAlilmCountResult {
        val basketList = loadBasketPort.loadBasket(command.member.id!!)

        val result = MyAlilmCountResult(
            count = basketList.size,
            alilmCount = basketList.filter { it.isAlilm }.size,
            basketCount = basketList.size,
        )

        return result
    }
}