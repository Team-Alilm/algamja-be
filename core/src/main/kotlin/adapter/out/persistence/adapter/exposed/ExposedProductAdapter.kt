package org.team_alilm.adapter.out.persistence.adapter.exposed

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.innerJoin

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.adapter.out.persistence.exposed.table.BasketExposedTable
import org.team_alilm.adapter.out.persistence.exposed.table.ProductExposedTable
import org.team_alilm.application.port.`in`.use_case.ProductSliceUseCase
import org.team_alilm.application.port.out.LoadFilteredProductListPort
import java.math.BigDecimal

@Component
@Transactional(readOnly = true)
class ExposedProductAdapter : LoadFilteredProductListPort {

    override fun getFilteredProductList(
        category: String?,
        size: Int,
        sort: String,
        sortKey: String?
    ): ProductSliceUseCase.CustomSlice {
        val waitingCount = BasketExposedTable.id.count().alias("waitingCount")

        val joined = ProductExposedTable.innerJoin(
            otherTable = BasketExposedTable,
            onColumn   = { ProductExposedTable.id },
            otherColumn= { BasketExposedTable.productId }
        )

        val baseConditions = mutableListOf(
            ProductExposedTable.isDeleted eq false,
            BasketExposedTable.isDeleted eq false,
            BasketExposedTable.isAlilm eq false,
            BasketExposedTable.isHidden eq false
        ).apply {
            category?.let { add(ProductExposedTable.firstCategory eq it) }
        }

        val (sortCol, sortOrder) = when (sort) {
            "PRICE_ASC"       -> ProductExposedTable.price to SortOrder.ASC
            "PRICE_DESC"      -> ProductExposedTable.price to SortOrder.DESC
            "WAITING_COUNT"   -> waitingCount to SortOrder.DESC
            else /*CREATED_DATE_DESC*/ -> ProductExposedTable.createdDate to SortOrder.DESC
        }

        val cursorCondition: Op<Boolean>? = sortKey?.let { key ->
            when (sort) {
                "PRICE_ASC" -> {
                    // 가격
                    val lastPrice = BigDecimal(key)
                    sortCol greater lastPrice
                }

                "PRICE_DESC" -> {
                    // 가격
                    val lastPrice = BigDecimal(key)
                    sortCol less lastPrice
                }

                "WAITING_COUNT" -> {
                    // 대기 수
                    val lastCount = key.toLong()
                    waitingCount less lastCount
                }

                "CREATED_DATE_DESC" -> {
                    // 생성일
                    val lastDate = key.toLong()
                    sortCol less lastDate
                }

                else -> null
            }
        }

        val baseFilter = baseConditions.reduce { acc, op -> acc and op }
        val fullFilter = cursorCondition?.let { baseFilter and it } ?: baseFilter

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
                waitingCount
            )
            .where { fullFilter }
            .groupBy(ProductExposedTable.id)
            .orderBy(sortCol to sortOrder)
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
                waitingCount = row[waitingCount]
            )
        }

        val hasNext = result.size > size

        return ProductSliceUseCase.CustomSlice(
            contents = result.take(size),
            hasNext = hasNext,
            size = size,
        )
    }
}