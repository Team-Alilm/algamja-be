package org.teamalilm.alilmbe.application.service

import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.application.port.`in`.use_case.BasketSliceUseCase
import org.teamalilm.alilmbe.application.port.out.product.LoadBasketSlicePort

@Service
@Transactional(readOnly = true)
class BasketSliceService (
    private val LoadProductSlicePort: LoadBasketSlicePort
) : BasketSliceUseCase {

    override fun basketSlice(command: BasketSliceUseCase.BasketListCommand): Slice<BasketSliceUseCase.BasketListResult> {
        TODO("Not yet implemented")
    }
}