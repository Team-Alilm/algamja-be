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
import org.team_alilm.domain.product.ProductCategory
import org.team_alilm.domain.product.ProductSortType

@Component
@Transactional(readOnly = true)
class ExposedProductAdapter : LoadFilteredProductListPort {

    override fun getFilteredProductListV3(
        size: Int,
        category: ProductCategory?,
        sort: ProductSortType,
        price: Int?,
        productId: Long?,
        waitingCount: Long?,
    ): ProductSliceUseCase.CustomSlice = executeQuery(
        size = size,
        category = category,
        sort = sort,
        price = price,
        productId = productId,
        waitingCount = waitingCount,
        useCursor = true
    )

    override fun getFilteredProductListV2(
        size: Int,
        page: Int,
        category: String?
    ): ProductSliceUseCase.CustomSlice = executeQuery(
        size = size,
        category = ProductCategory.valueOf(category!!),
        page = page,
        sort = ProductSortType.WAITING_COUNT_DESC,
        useCursor = false
    )

    private fun executeQuery(
        size: Int,
        category: ProductCategory?,
        sort: ProductSortType,
        price: Int? = null,
        productId: Long? = null,
        waitingCount: Long? = null,
        page: Int = 0,
        useCursor: Boolean
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
            category?.let { add(ProductExposedTable.firstCategory eq it.description) }
        }

        val (sortCol, sortOrder) = when (sort) {
            ProductSortType.PRICE_ASC -> ProductExposedTable.price to SortOrder.ASC
            ProductSortType.PRICE_DESC -> ProductExposedTable.price to SortOrder.DESC
            ProductSortType.WAITING_COUNT_DESC -> waitingCountAlias to SortOrder.DESC
            ProductSortType.CREATED_DATE_DESC -> ProductExposedTable.id to SortOrder.DESC
        }

        val cursorCondition = when (sort) {
            ProductSortType.PRICE_ASC -> price?.let { p -> productId?.let { ProductExposedTable.price greater p or (ProductExposedTable.price eq p and (ProductExposedTable.id greater it)) } }
            ProductSortType.PRICE_DESC -> price?.let { p -> productId?.let { ProductExposedTable.price less p or (ProductExposedTable.price eq p and (ProductExposedTable.id greater it)) } }
            ProductSortType.WAITING_COUNT_DESC -> productId?.let { ProductExposedTable.id greater it }
            ProductSortType.CREATED_DATE_DESC -> productId?.let { ProductExposedTable.id less it }
        }

        val havingFilter = if (sort == ProductSortType.WAITING_COUNT_DESC && waitingCount != null && productId != null) {
            (countExpr less waitingCount) or ((countExpr eq waitingCount) and (ProductExposedTable.id less productId))
        } else null

        val filter = baseConditions.reduce { acc, op -> acc and op }
        val fullFilter = cursorCondition?.let { filter and it } ?: filter

        val query = joined.select(
            ProductExposedTable.id,
            ProductExposedTable.storeNumber,
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
        ).where { fullFilter }
            .groupBy(ProductExposedTable.id)
            .apply { havingFilter?.let { having { it } } }
            .orderBy(sortCol to sortOrder, ProductExposedTable.id to SortOrder.DESC)

        if (useCursor) {
            query.limit(size + 1)
        } else {
            query.limit(size).offset((page * size).toLong())
        }

        val result = query.map { row ->
            ProductSliceUseCase.ProductSliceResult(
                id = row[ProductExposedTable.id].value,
                number = row[ProductExposedTable.storeNumber],
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
            size = result.take(size).size
        )
    }
}