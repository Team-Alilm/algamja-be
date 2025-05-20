package org.team_alilm.adapter.out.persistence.adapter.exposed

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.adapter.out.persistence.exposed.table.BasketExposedTable
import org.team_alilm.adapter.out.persistence.exposed.table.ProductExposedTable
import org.team_alilm.application.port.`in`.use_case.ProductSliceUseCase
import org.team_alilm.application.port.out.LoadFilteredProductListPort

@Component
@Transactional(readOnly = true)
class ExposedProductAdapter : LoadFilteredProductListPort {

    override fun getFilteredProductList(
        size: Int,
        category: String?,
        sort: String,
        price: Int?,
        productId: Long?,
        waitingCount: Long?,
    ): ProductSliceUseCase.CustomSlice {

        val countExpr = BasketExposedTable.id.count()
        val waitingCountAlias = countExpr.alias("waitingCount")

        val joined = ProductExposedTable.innerJoin(BasketExposedTable) {
            ProductExposedTable.id eq BasketExposedTable.productId
        }

        val baseConditions = buildList {
            add(ProductExposedTable.isDeleted eq false)
            add(BasketExposedTable.isDeleted eq false)
            add(BasketExposedTable.isAlilm eq false)
            add(BasketExposedTable.isHidden eq false)
            category?.let { add(ProductExposedTable.firstCategory eq it) }
        }

        val (sortCol, sortOrder) = when (sort) {
            "PRICE_ASC"       -> ProductExposedTable.price to SortOrder.ASC
            "PRICE_DESC"      -> ProductExposedTable.price to SortOrder.DESC
            "WAITING_COUNT"   -> waitingCountAlias to SortOrder.DESC
            else               -> ProductExposedTable.createdDate to SortOrder.DESC
        }

        val cursorCondition = when (sort) {
            "PRICE_ASC" -> {
                if (price != null && productId != null) {
                    (ProductExposedTable.price greater price) or
                            ((ProductExposedTable.price eq price) and (ProductExposedTable.id greater productId))
                } else null
            }
            "PRICE_DESC" -> {
                if (price != null && productId != null) {
                    (ProductExposedTable.price less price) or
                            ((ProductExposedTable.price eq price) and (ProductExposedTable.id greater  productId))
                } else null
            }
            "CREATED_DATE_DESC" -> {
                if (productId != null) {
                    ProductExposedTable.id less productId
                } else null
            }
            else -> null
        }

        val filter = baseConditions.reduce { acc, op -> acc and op }
        val fullFilter = cursorCondition?.let { filter and it } ?: filter

        val havingFilter = if (sort == "WAITING_COUNT" && waitingCount != null && productId != null) {
            (countExpr less waitingCount) or
                    ((countExpr eq waitingCount) and (ProductExposedTable.id greater productId))
        } else null

        val rows = joined
            .select(
                ProductExposedTable.id,
                ProductExposedTable.number,
                ProductExposedTable.name,
                ProductExposedTable.brand,
                ProductExposedTable.thumbnailUrl,
                ProductExposedTable.store,
                ProductExposedTable.price,
                ProductExposedTable.firstCategory,
                ProductExposedTable.secondCategory,
                ProductExposedTable.firstOption,
                ProductExposedTable.secondOption,
                ProductExposedTable.thirdOption,
                waitingCountAlias
            )
            .where { fullFilter }
            .groupBy(ProductExposedTable.id)
            .apply { havingFilter?.let { having { it } } }
            .orderBy(
                sortCol to sortOrder,
                ProductExposedTable.id to SortOrder.ASC
            )
            .limit(size + 1)

        val result = rows.map { row ->
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
                waitingCount = row[waitingCountAlias]
            )
        }

        val hasNext = result.size > size

        return ProductSliceUseCase.CustomSlice(
            contents = result.take(size),
            hasNext = hasNext,
            size = result.take(size).size,
        )
    }
}
