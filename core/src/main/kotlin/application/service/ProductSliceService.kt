package org.team_alilm.application.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.ProductSliceUseCase
import org.team_alilm.application.port.`in`.use_case.ProductSliceUseCase.ProductSliceResult
import org.team_alilm.application.port.out.LoadProductPort

@Service
@Transactional(readOnly = true)
class ProductSliceService (
    private val loadProductPort: LoadProductPort
) : ProductSliceUseCase {

    override fun productSlice(command: ProductSliceUseCase.ProductSliceCommand): ProductSliceUseCase.CustomSlice {

        val productSlice = loadProductPort.loadProductSlice(
            PageRequest.of(
                command.page,
                command.size,
            )
        )

        return ProductSliceUseCase.CustomSlice(
            contents = productSlice.content.map {
                ProductSliceResult.from(product = it.product, waitingCount = it.waitingCount)
            },
            hasNext = productSlice.hasNext(),
            isLast = productSlice.isLast,
            number = productSlice.number,
            size = productSlice.size
        )
    }

}