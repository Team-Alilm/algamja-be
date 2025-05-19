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
        val lastProduct = product(command.lastProductId)

        val productSlice = loadFilteredProductListPort.getFilteredProductList(
            size = command.size,
            category = command.category,
            sort = command.sort,
            sortKey = getSortKey(command.sort, lastProduct, command.waitingCount),
        )

        return productSlice
    }

    private fun getSortKey(
        sort: String,
        lastProduct: Product?,
        waitingCount: Long?
    ): String? = when(sort) {
            "WAITING_COUNT" -> waitingCount?.toString()
            "LATEST" -> lastProduct?.createdDate.toString()
            "PRICE_ASC" -> lastProduct?.price.toString()
            "PRICE_DESC" -> lastProduct?.price.toString()
            else -> throw IllegalArgumentException("Invalid sort type: $sort")
    }

    private fun product(productId: Long?) = productId
        ?.let { loadProductPort.loadProduct(it) }
}