package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.ProductSliceUseCase
import org.team_alilm.application.port.out.LoadFilteredProductListPort

@Service
@Transactional(readOnly = true)
class ProductSliceService (
    private val loadFilteredProductListPort: LoadFilteredProductListPort,
) : ProductSliceUseCase {

    override fun productSlice(command: ProductSliceUseCase.ProductSliceCommand): ProductSliceUseCase.CustomSlice {

        val productSlice = loadFilteredProductListPort.getFilteredProductList(
            size = command.size,
            category = command.category,
            sort = command.sort,
            lastProductId = command.lastProductId,
        )

        return productSlice
    }
}