package org.team_alilm.application.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.ProductSliceUseCase
import org.team_alilm.application.port.out.LoadBasketPort
import org.team_alilm.application.port.out.LoadProductSlicePort

@Service
@Transactional(readOnly = true)
class ProductSliceService (
    private val loadProductSlicePort: LoadProductSlicePort,
    private val loadBasketPort: LoadBasketPort
) : ProductSliceUseCase {

    override fun productSlice(command: ProductSliceUseCase.ProductSliceCommand): ProductSliceUseCase.CustomSlice {

        val productSlice = loadProductSlicePort.loadProductSlice(
            PageRequest.of(
                command.page,
                command.size,
                by("createdDate").descending()
            )
        )

        val contents = productSlice.content.map {
            val waitingCount = loadBasketPort.loadBasketCount(it.id!!)
            ProductSliceUseCase.ProductSliceResult.from(it, waitingCount)
        }.filter { it.waitingCount > 0 }

        return ProductSliceUseCase.CustomSlice(
            contents = contents,
            hasNext = productSlice.hasNext(),
            isLast = productSlice.isLast,
            number = productSlice.number,
            size = productSlice.size
        )
    }

}