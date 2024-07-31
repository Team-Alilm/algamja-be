package org.teamalilm.alilmbe.application.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.application.port.`in`.use_case.BasketSliceUseCase
import org.teamalilm.alilmbe.application.port.`in`.use_case.BasketSliceUseCase.*
import org.teamalilm.alilmbe.application.port.out.LoadBasketSlicePort

@Service
@Transactional(readOnly = true)
class BasketSliceService (
    private val loadProductSlicePort: LoadBasketSlicePort
) : BasketSliceUseCase {

    override fun basketSlice(command: BasketListCommand): Slice<BasketListResult> {

        val basketCountProjection = loadProductSlicePort.loadBasketSlice(
            PageRequest.of(
                command.page,
                command.size,
            )
        )

        return basketCountProjection.map {
            BasketListResult(
                id = it.product.id?.value!!,
                number = it.product.number,
                name = it.product.name,
                brand = it.product.brand,
                imageUrl = it.product.imageUrl,
                store = it.product.store.name,
                price = it.product.price,
                category = it.product.category,
                option1 = it.product.option1,
                option2 = it.product.option2,
                option3 = it.product.option3,
                waitingCount = it.waitingCount
            )
        }
    }

}