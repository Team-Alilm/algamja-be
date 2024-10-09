package org.team_alilm.application.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.BasketSliceUseCase
import org.team_alilm.application.port.`in`.use_case.BasketSliceUseCase.*
import org.team_alilm.application.port.out.LoadSliceBasketPort

@Service
@Transactional(readOnly = true)
class BasketSliceService (
    private val loadProductSlicePort: LoadSliceBasketPort
) : BasketSliceUseCase {

    override fun basketSlice(command: BasketListCommand): CustomSlice {

        val basketCountProjectionSlice = loadProductSlicePort.loadBasketSlice(
            PageRequest.of(
                command.page,
                command.size,
            )
        )

        return CustomSlice(
            contents = basketCountProjectionSlice.content.map { BasketListResult.from(it) },
            hasNext = basketCountProjectionSlice.hasNext(),
            isLast = basketCountProjectionSlice.isLast(),
            number = basketCountProjectionSlice.number,
            size = basketCountProjectionSlice.size
        )
    }

}