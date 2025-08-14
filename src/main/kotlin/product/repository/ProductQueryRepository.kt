package org.team_alilm.product.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Repository
import org.team_alilm.product.entity.ProductTable
import org.team_alilm.basket.exposed.BasketTable
import org.team_alilm.common.enums.Sort
import org.team_alilm.product.controller.v1.dto.param.ProductListParam
import org.team_alilm.product.repository.projection.ProductProjection

@Repository
class ProductQueryRepository {

    fun sliceProducts(param: ProductListParam): Slice<ProductProjection> = transaction {
        val where = createProductSearchCondition(param) and createCursorPaginationCondition(param) and ProductTable.notDeleted()

        val ordered = when (param.sort) {
            Sort.PRICE_ASC  -> baseSelect(where).orderBy(
                ProductTable.price to SortOrder.ASC,
                ProductTable.id to SortOrder.DESC
            )
            Sort.PRICE_DESC -> baseSelect(where).orderBy(
                ProductTable.price to SortOrder.DESC,
                ProductTable.id to SortOrder.DESC
            )
            Sort.CREATED_DATE_DESC,
            Sort.WAITING_COUNT_DESC -> baseSelect(where).orderBy(
                ProductTable.id to SortOrder.DESC
            )
        }

        val rows   = ordered.limit(param.size + 1).toList()
        val hasNext = rows.size > param.size
        val pageRows = if (hasNext) rows.dropLast(1) else rows

        val content = pageRows.map { r ->
            ProductProjection(
                id = r[ProductTable.id],
                storeNumber = r[ProductTable.storeNumber],
                name = r[ProductTable.name],
                brand = r[ProductTable.brand],
                thumbnailUrl = r[ProductTable.thumbnailUrl],
                store = r[ProductTable.store],
                price = r[ProductTable.price].toInt(),
                firstCategory = r[ProductTable.firstCategory],
                secondCategory = r[ProductTable.secondCategory],
                firstOption = r[ProductTable.firstOption],
                secondOption = r[ProductTable.secondOption],
                thirdOption = r[ProductTable.thirdOption]
            )
        }

        SliceImpl(content, Pageable.ofSize(param.size), hasNext)
    }

    // 최근 알림 발송한 상품 상위 10개 조회 ( 중복 없음 )
    fun getTop10RecentlyNotifiedProductIds(): List<Long> = transaction {
        BasketTable
            .select(BasketTable.productId, BasketTable.notificationDate.max())
            .where { BasketTable.isNotification eq true }
            .groupBy(BasketTable.productId)
            .orderBy(BasketTable.notificationDate, SortOrder.DESC)
            .limit(10)
            .map { it[BasketTable.productId] }
    }

    private fun baseSelect(where: Op<Boolean>) =
        ProductTable.select(
            ProductTable.id,
            ProductTable.name,
            ProductTable.brand,
            ProductTable.thumbnailUrl,
            ProductTable.store,
            ProductTable.price,
            ProductTable.firstCategory,
            ProductTable.secondCategory,
            ProductTable.firstOption,
            ProductTable.secondOption,
            ProductTable.thirdOption
        ).where { where }

    private fun createProductSearchCondition(param: ProductListParam): Op<Boolean> {
        var w: Op<Boolean> = Op.TRUE
        if (!param.keyword.isNullOrBlank()) {
            val kw = "%${param.keyword.trim()}%"
            w = w and ((ProductTable.name like kw) or (ProductTable.brand like kw))
        }
        if (!param.category.isNullOrBlank()) {
            val cat = param.category
            w = w and ((ProductTable.firstCategory eq cat) or (ProductTable.secondCategory eq cat))
        }
        return w
    }

    private fun createCursorPaginationCondition(param: ProductListParam): Op<Boolean> = with(SqlExpressionBuilder) {
        when (param.sort) {
            Sort.PRICE_ASC -> {
                val lp = param.lastPrice?.toBigDecimal()
                val lid = param.lastProductId
                if (lp == null || lid == null) Op.TRUE
                else (ProductTable.price greater lp) or
                        ((ProductTable.price eq lp) and (ProductTable.id less lid))
            }
            Sort.PRICE_DESC -> {
                val lp = param.lastPrice?.toBigDecimal()
                val lid = param.lastProductId
                if (lp == null || lid == null) Op.TRUE
                else (ProductTable.price less lp) or
                        ((ProductTable.price eq lp) and (ProductTable.id less lid))
            }
            Sort.CREATED_DATE_DESC -> param.lastProductId?.let { ProductTable.id less it } ?: Op.TRUE
            Sort.WAITING_COUNT_DESC -> Op.TRUE // 서비스에서 대기수만 집계
        }
    }
}