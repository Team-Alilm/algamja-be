package org.team_alilm.adapter.out.persistence.adapter.exposed

import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.less
import org.jetbrains.exposed.v1.core.alias
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.slice
import org.jetbrains.exposed.v1.core.innerJoin
import org.jetbrains.exposed.v1.*
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.adapter.out.persistence.exposed.table.BasketExposedTable
import org.team_alilm.adapter.out.persistence.exposed.table.ProductExposedTable
import org.team_alilm.application.port.`in`.use_case.ProductSliceUseCase
import org.team_alilm.application.port.out.LoadFilteredProductListPort

@Component
class ExposedProductAdapter : LoadFilteredProductListPort {

    @Transactional(readOnly = true)
    override fun getFilteredProductList(
        category: String?,
        size: Int,
        sort: String,
        lastProductId: Long?
    ): ProductSliceUseCase.CustomSlice {
        val waitingCount = BasketExposedTable.id

        val joinedTables = ProductExposedTable.innerJoin(
            BasketExposedTable,
            onColumn = { ProductExposedTable.id },
            otherColumn = { BasketExposedTable.productId }
        )

        val baseConditions = mutableListOf<Op<Boolean>>(
            ProductExposedTable.isDeleted eq false,
            BasketExposedTable.isDeleted eq false,
            BasketExposedTable.isAlilm eq false,
            BasketExposedTable.isHidden eq false
        )

        category?.let {
            baseConditions += ProductExposedTable.firstCategory eq it
        }

        lastProductId?.let {
            baseConditions += ProductExposedTable.id less it
        }

        val query = joinedTables
            .slice(
                ProductExposedTable.id,
                ProductExposedTable.name,
                ProductExposedTable.brand,
                ProductExposedTable.thumbnailUrl,
                ProductExposedTable.price,
                ProductExposedTable.firstCategory,
                ProductExposedTable.secondCategory,
                ProductExposedTable.firstOption,
                ProductExposedTable.secondOption,
                ProductExposedTable.thirdOption,
                ProductExposedTable.store,
                ProductExposedTable.createdDate,
                ProductExposedTable.lastModifiedDate,
                ProductExposedTable.isDeleted,
                BasketExposedTable.createdDate,
                waitingCount.alias("waitingCount")
            )
            .select { baseConditions.reduce { acc, op -> acc and op } }
            .groupBy(ProductExposedTable.id)
            .orderBy
        (
                when (sort) {
                    "WAITING_COUNT" -> waitingCount to SortOrder.DESC
                    "PRICE_ASC" -> ProductExposedTable.price to SortOrder.ASC
                    "PRICE_DESC" -> ProductExposedTable.price to SortOrder.DESC
                    "CREATED_DATE_DESC" -> BasketExposedTable.createdDate to SortOrder.DESC
                    else -> waitingCount to SortOrder.DESC
                }
            )
            .limit(size + 1)

        val result = query.map { row ->
            ProductSliceUseCase.ProductSliceResult(
                id = row[ProductExposedTable.id].value,
                number = row[ProductExposedTable.number],
                name = row[ProductExposedTable.name],
                brand = row[ProductExposedTable.brand],
                thumbnailUrl = row[ProductExposedTable.thumbnailUrl],
                store = row[ProductExposedTable.store],
                price = row[ProductExposedTable.price],
                firstCategory = row[ProductExposedTable.firstCategory],
                secondCategory = row[ProductExposedTable.secondCategory],
                firstOption = row[ProductExposedTable.firstOption],
                secondOption = row[ProductExposedTable.secondOption],
                thirdOption = row[ProductExposedTable.thirdOption],
                waitingCount = row[waitingCount.alias("waitingCount")]
            )
        }

        val hasNext = result.size > size

        return ProductSliceUseCase.CustomSlice(
            contents = result.take(size),
            hasNext = hasNext,
            size = size
        )
    }
}