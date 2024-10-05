package org.teamalilm.alilm.application.service

import org.springframework.stereotype.Service
import org.teamalilm.alilm.application.port.`in`.use_case.MyAlilmCountUseCase
import org.teamalilm.alilm.application.port.`in`.use_case.MyAlilmCountUseCase.MyAlilmCountResult
import org.teamalilm.alilm.application.port.out.LoadBasketPort

@Service
class MyAlilmCountService(
    private val loadBasketPort: LoadBasketPort
) : MyAlilmCountUseCase {

    override fun myAlilmCount(command: MyAlilmCountUseCase.MyAlilmCountCommand): MyAlilmCountResult {
        val basketList = loadBasketPort.loadBasket(command.member.id!!)

        val result = MyAlilmCountResult(
            count = basketList.size
        )

        return result
    }
}