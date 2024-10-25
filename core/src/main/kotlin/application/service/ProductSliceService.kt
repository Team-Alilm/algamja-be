package org.team_alilm.application.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.ProductSliceUseCase
import org.team_alilm.application.port.out.LoadProductSlicePort

@Service
@Transactional(readOnly = true)
class ProductSliceService (
    private val loadProductSlicePort: LoadProductSlicePort
) : ProductSliceUseCase {

    override fun productSlice(command: ProductSliceUseCase.ProductSliceCommand): ProductSliceUseCase.CustomSlice {

        val basketCountProjectionSlice = loadProductSlicePort.loadProductSlice(
            PageRequest.of(
                command.page,
                command.size,
            )
        )

        return ProductSliceUseCase.CustomSlice(
            contents = basketCountProjectionSlice.content.map { ProductSliceUseCase.ProductSliceResult.from(it) },
            hasNext = basketCountProjectionSlice.hasNext(),
            isLast = basketCountProjectionSlice.isLast,
            number = basketCountProjectionSlice.number,
            size = basketCountProjectionSlice.size
        )
    }

}