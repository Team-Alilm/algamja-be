package org.team_alilm.application.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.ProductSliceUseCase
import org.team_alilm.application.port.`in`.use_case.ProductSliceUseCase.ProductSliceResult
import org.team_alilm.application.port.out.LoadFilteredProductListPort
import org.team_alilm.application.port.out.LoadProductSlicePort

@Service
@Transactional(readOnly = true, transactionManager = "springTransactionManager")
class ProductSliceService (
    private val loadFilteredProductListPort: LoadFilteredProductListPort,
    private val loadProductSlicePort: LoadProductSlicePort
) : ProductSliceUseCase {

    override fun productSlice(command: ProductSliceUseCase.ProductSliceCommand): ProductSliceUseCase.CustomSlice {
        val productSlice = loadFilteredProductListPort.getFilteredProductList(
            size = command.size,
            category = command.category,
            sort = command.sort,
            price = command.price,
            productId = command.lastProductId,
            waitingCount = command.waitingCount,
        )

        return productSlice
    }

//    override fun productSliceV2(command: ProductSliceUseCase.ProductSliceCommandV2): ProductSliceUseCase.CustomSlice {
//        loadProductSlicePort.loadProductSlice(
//            pageRequest = PageRequest.of(command.page, command.size),
//            category = command.category,
//        ).let {
//            return ProductSliceUseCase.CustomSlice(
//                contents = it.map { ProductSliceResult.from(
//                    product = it.product,
//                    waitingCount = it.waitingCount,
//                )}.toList(),
//                hasNext = it.hasNext(),
//                size = it.size
//            )
//        }
//    }
}