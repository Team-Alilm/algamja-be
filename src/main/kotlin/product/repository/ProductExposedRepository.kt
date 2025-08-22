package org.team_alilm.product.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.springframework.stereotype.Repository
import org.team_alilm.basket.entity.BasketTable
import org.team_alilm.common.enums.Sort.*
import org.team_alilm.product.controller.v1.dto.param.ProductListParam
import org.team_alilm.product.entity.ProductRow
import org.team_alilm.product.entity.ProductTable
import org.team_alilm.product.repository.projection.ProductSliceProjection

@Repository
class ProductExposedRepository {

    /** 공통 WHERE 빌더 (목록/카운트에서 재사용) */
    private fun buildBaseWhere(param: ProductListParam): Op<Boolean> {
        val table = ProductTable
        val like  = param.keyword?.trim()?.takeIf { it.isNotEmpty() }?.let { "%$it%" }
        val cat   = param.category?.trim()?.takeIf { it.isNotEmpty() }

        return listOfNotNull(
            table.isDelete eq false,
            like?.let { (table.name like it) or (table.brand like it) },
            cat?.let { table.firstCategory eq it }
        ).fold(initial = Op.TRUE as Op<Boolean>) { acc, op -> acc and op }
    }

    /** 상품 목록 조회 (커서 기반, +1 로 hasNext 판단) */
    fun fetchProducts(param: ProductListParam): ProductSliceProjection {
        val table = ProductTable
        val pageSize = param.size?.coerceIn(1, 100) ?: 20
        val baseWhere = buildBaseWhere(param)

        val (orders, cursor) = when (param.sort) {
            PRICE_ASC -> {
                val cur = if (param.lastPrice != null && param.lastProductId != null) {
                    val lastPrice = param.lastPrice.toBigDecimal()
                    (table.price greater lastPrice) or
                            ((table.price eq lastPrice) and (table.id less param.lastProductId))
                } else null
                listOf(table.price to SortOrder.ASC, table.id to SortOrder.DESC) to cur
            }
            PRICE_DESC -> {
                val cur = if (param.lastPrice != null && param.lastProductId != null) {
                    val lastPrice = param.lastPrice.toBigDecimal()
                    (table.price less lastPrice) or
                            ((table.price eq lastPrice) and (table.id less param.lastProductId))
                } else null
                listOf(table.price to SortOrder.DESC, table.id to SortOrder.DESC) to cur
            }
            CREATED_DATE_DESC, null -> {
                val cur = param.lastProductId?.let { table.id less it }
                listOf(table.id to SortOrder.DESC) to cur
            }
            WAITING_COUNT_DESC -> error("WAITING_COUNT_DESC는 별도 집계 메서드에서 처리하세요.")
        }

        val finalWhere = cursor?.let { baseWhere and it } ?: baseWhere

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

    /** 대기자수 DESC 정렬 (집계 서브쿼리 JOIN) */
    fun fetchProductsOrderByWaitingCountDesc(param: ProductListParam): ProductSliceProjection {
        val table = ProductTable
        val pageSize = param.size?.coerceIn(1, 100) ?: 20
        val baseWhere = buildBaseWhere(param)

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

        val rows = table
            .join(basketAgg, JoinType.LEFT, additionalConstraint = { table.id eq aggPidCol })
            .selectAll()
            .where { baseWhere }
            .orderBy(waitingCol to SortOrder.DESC, table.id to SortOrder.DESC)
            .limit(pageSize + 1)
            .toList()

        val hasNext = rows.size > pageSize
        val pageRows = if (hasNext) rows.take(pageSize) else rows

        return ProductSliceProjection(
            productRows = pageRows.map(ProductRow::from),
            hasNext = hasNext
        )
    }

    /** 같은 필터로 ‘총 개수’ 조회 (무한스크롤용 별도 API에서 사용 권장) */
    fun countProducts(param: ProductListParam): Long {
        val predicate = buildBaseWhere(param)
        val cnt = ProductTable.id.count()
        return ProductTable
            .select(cnt)
            .where { predicate }
            .firstOrNull()
            ?.get(cnt)
            ?: 0L
    }

    /** 카테고리 기준 유사 상품 상위 10개 (자기 자신 제외), 최신(id DESC) */
    fun fetchTop10SimilarProducts(
        excludeId: Long,
        firstCategory: String,
        secondCategory: String?
    ): List<ProductRow> {
        val cats = buildList {
            add(firstCategory.trim())
            secondCategory?.trim()?.takeIf { it.isNotEmpty() }?.let { add(it) }
        }

        val categoryPredicate =
            if (cats.size == 1) {
                (ProductTable.firstCategory eq cats.first()) or
                (ProductTable.secondCategory eq cats.first())
            } else {
                (ProductTable.firstCategory inList cats) or
                (ProductTable.secondCategory inList cats)
            }

        return ProductTable
            .selectAll()
            .where {
                (ProductTable.isDelete eq false) and
                (ProductTable.id neq excludeId) and
                categoryPredicate
            }
            .orderBy(ProductTable.id to SortOrder.DESC)
            .limit(10)
            .map(ProductRow::from)
    }

    fun fetchProductsByIds(productIds: List<Long>): List<ProductRow> =
        ProductTable
            .selectAll()
            .where {
                (ProductTable.id inList productIds) and
                (ProductTable.isDelete eq false)
            }
            .map(ProductRow::from)

    /** 단건 조회 */
    fun fetchProductById(productId: Long): ProductRow? =
        ProductTable
            .selectAll()
            .where { (ProductTable.id eq productId) and (ProductTable.isDelete eq false) }
            .singleOrNull()
            ?.let(ProductRow::from)
}