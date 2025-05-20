package org.team_alilm.application.service

import domain.product.Product
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.ProductSliceUseCase
import org.team_alilm.application.port.out.LoadFilteredProductListPort
import org.team_alilm.application.port.out.LoadProductPort

@Service
@Transactional(readOnly = true, transactionManager = "springTransactionManager")
class ProductSliceService (
    private val loadFilteredProductListPort: LoadFilteredProductListPort,
    private val loadProductPort: LoadProductPort,
) : ProductSliceUseCase {

    override fun productSlice(command: ProductSliceUseCase.ProductSliceCommand): ProductSliceUseCase.CustomSlice {
        val productSlice = loadFilteredProductListPort.getFilteredProductList(
            size = command.size,
            category = command.category,
            sort = command.sort,
            price = command.price,
            productId = command.lastProductId,
            waitingCount = command.waitingCount,
            createdDate = command.createdDate
        )

        return productSlice
    }

    private fun getSortKey(
        sort: String,
        price: Long?,
        waitingCount: Long?,
    ): String? = when(sort) {
        "WAITING_COUNT" -> waitingCount
        "LATEST" -> price
        "PRICE_ASC" -> price
        "PRICE_DESC" -> price
        else -> throw IllegalArgumentException("Invalid sort type: $sort")
    } as String?

    private fun product(productId: Long?) = productId
        ?.let { loadProductPort.loadProduct(it) }
}