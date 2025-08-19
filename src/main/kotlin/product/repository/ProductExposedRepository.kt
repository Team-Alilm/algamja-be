package org.team_alilm.product.repository

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.team_alilm.product.controller.v1.dto.param.ProductListParam
import org.team_alilm.product.entity.ProductRow
import org.team_alilm.product.entity.ProductTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.team_alilm.basket.entity.BasketTable
import org.team_alilm.common.enums.Sort
import org.team_alilm.product.repository.projection.ProductSliceProjection

@Repository
class ProductExposedRepository {

    // 상품 목록 조회
    fun fetchProducts(param: ProductListParam): ProductSliceProjection {
        val table = ProductTable
        val pageSize = param.size.coerceIn(1, 100)

        // WHERE
        val like = param.keyword?.trim()?.takeIf { it.isNotEmpty() }?.let { "%$it%" }
        val cat  = param.category?.trim()?.takeIf { it.isNotEmpty() }

        val baseWhere = listOfNotNull<Op<Boolean>>(
            table.isDelete eq false,
            like?.let { (table.name like it) or (table.brand like it) },
            cat?.let { table.firstCategory eq it }
        ).fold(Op.TRUE as Op<Boolean>) { acc, op -> acc and op }

        // 정렬 & 커서
        val (orders, cursor) = when (param.sort) {
            Sort.PRICE_ASC -> {
                val cur = if (param.lastPrice != null && param.lastProductId != null) {
                    val lastPrice = param.lastPrice.toBigDecimal()
                    (table.price greater lastPrice) or
                            ((table.price eq lastPrice) and (table.id less param.lastProductId))
                } else null
                listOf(table.price to SortOrder.ASC, table.id to SortOrder.DESC) to cur
            }
            Sort.PRICE_DESC -> {
                val cur = if (param.lastPrice != null && param.lastProductId != null) {
                    val lastPrice = param.lastPrice.toBigDecimal()
                    (table.price less lastPrice) or
                            ((table.price eq lastPrice) and (table.id less param.lastProductId))
                } else null
                listOf(table.price to SortOrder.DESC, table.id to SortOrder.DESC) to cur
            }
            Sort.CREATED_DATE_DESC -> {
                val cur = param.lastProductId?.let { table.id less it }
                listOf(table.id to SortOrder.DESC) to cur
            }
            Sort.WAITING_COUNT_DESC -> error("WAITING_COUNT_DESC는 별도 집계 메서드에서 처리하세요.")
        }

        val finalWhere = cursor?.let { baseWhere and it } ?: baseWhere

        // SELECT (+1 for hasNext)
        val rows = table
            .selectAll()
            .where { finalWhere }
            .orderBy(*orders.toTypedArray())
            .limit(pageSize + 1)
            .toList()

        val hasNext = rows.size > pageSize
        val pageRows = if (hasNext) rows.take(pageSize) else rows

        return ProductSliceProjection(
            productRows = pageRows.map(ProductRow::from),
            hasNext = hasNext
        )
    }

    fun fetchProductsOrderByWaitingCountDesc(param: ProductListParam): ProductSliceProjection {
        val table = ProductTable

        // 1) Product WHERE 조건 구성
        val likeKeyword = param.keyword?.trim()?.takeIf { it.isNotEmpty() }?.let { "%$it%" }
        val category    = param.category?.trim()?.takeIf { it.isNotEmpty() }

        val conditions: List<Op<Boolean>> = listOfNotNull(
            table.isDelete eq false,
            likeKeyword?.let { (table.name like it) or (table.brand like it) },
            category?.let { table.firstCategory eq it }
        )
        val baseWhere = conditions.reduceOrNull { acc, op -> acc and op } ?: Op.TRUE

        // 2) Basket 집계 서브쿼리 (product_id 별 waiting_count)
        val waitingCountExpr = BasketTable.id.count().alias("waiting_count")
        val basketAgg = BasketTable
            .select(BasketTable.productId, waitingCountExpr)
            .where {
                (BasketTable.isNotification eq false) and
                        (BasketTable.isHidden eq false) and
                        (BasketTable.isDelete eq false)
            }
            .groupBy(BasketTable.productId)
            .alias("basket_agg")

        val waitingCol = basketAgg[waitingCountExpr]
        val aggPidCol  = basketAgg[BasketTable.productId]

        // 3) JOIN + SELECT (+1 로 더 읽어 hasNext 판별)
        val rows = table
            .join(basketAgg, JoinType.LEFT, additionalConstraint = { table.id eq aggPidCol })
            .selectAll()
            .where { baseWhere }
            .orderBy(waitingCol to SortOrder.DESC, table.id to SortOrder.DESC)
            .limit(param.size + 1)
            .toList()
        val hasNext = rows.size > param.size
        val pageRows = if (hasNext) rows.take(param.size) else rows

        return ProductSliceProjection(
            productRows = pageRows.map(ProductRow::from),
            hasNext = hasNext
        )
    }

    fun fetchProductsByIds(productIds: List<Long>) : List<ProductRow> =
        ProductTable
            .selectAll()
            .where {
                (ProductTable.id inList productIds) and
                        (ProductTable.isDelete eq false)
            }
            .map(ProductRow::from)

    /** 단건 상품 상세 조회 (isDelete = false 조건 포함) */
    fun fetchProductById(productId: Long): ProductRow? =
        ProductTable
            .selectAll()
            .where {
                (ProductTable.id eq productId) and
                        (ProductTable.isDelete eq false)
            }
            .singleOrNull()   // 0개 또는 1개만 허용
            ?.let(ProductRow::from)
}