package org.team_alilm.product.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.springframework.stereotype.Repository
import org.team_alilm.basket.entity.BasketTable
import org.team_alilm.common.enums.Sort.*
import org.team_alilm.common.enums.Store
import org.team_alilm.product.controller.v1.dto.param.ProductListParam
import org.team_alilm.product.crawler.dto.CrawledProduct
import org.team_alilm.product.entity.ProductRow
import org.team_alilm.product.entity.ProductTable
import org.team_alilm.product.repository.projection.ProductSliceProjection

@Repository
class ProductExposedRepository {

    /**
             * Retrieves a single non-deleted product matching the composite key (store, storeNumber, firstOption, secondOption, thirdOption).
             *
             * Nullable option parameters are compared null-safely: a null value matches SQL IS NULL for that column.
             *
             * @param store The store enum value.
             * @param storeNumber The store-specific product number.
             * @param firstOption Nullable first option to match; null matches rows where `firstOption IS NULL`.
             * @param secondOption Nullable second option to match; null matches rows where `secondOption IS NULL`.
             * @param thirdOption Nullable third option to match; null matches rows where `thirdOption IS NULL`.
             * @return A mapped ProductRow if a matching non-deleted product exists, or null otherwise.
             */
    fun fetchByCompositeKey(
        store: Store,
        storeNumber: Long,
        firstOption: String?,
        secondOption: String?,
        thirdOption: String?
    ): ProductRow? =
        ProductTable
            .selectAll()
            .where {
                (ProductTable.isDelete eq false) and
                        (ProductTable.store eq store) and
                        (ProductTable.storeNumber eq storeNumber) and
                        ProductTable.firstOption.eqNullable(firstOption) and
                        ProductTable.secondOption.eqNullable(secondOption) and
                        ProductTable.thirdOption.eqNullable(thirdOption)
            }
            .singleOrNull()
            ?.let(ProductRow::from)

    /**
     * Builds the base SQL WHERE predicate for product list and count queries.
     *
     * The returned predicate always requires `isDelete = false`. If `param.keyword` is a
     * non-empty string it adds a `LIKE` condition matching the keyword against `name` or `brand`.
     * If `param.category` is a non-empty string it adds an equality condition on `firstCategory`.
     *
     * @param param Filtering and pagination parameters; only `keyword` and `category` are used.
     * @return An Exposed `Op<Boolean>` representing the conjunction of applicable predicates.
     */
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

    /**
             * Retrieves a non-deleted product by its ID.
             *
             * @param productId the product's database ID
             * @return the matching ProductRow, or null if no non-deleted product exists with the given ID
             */
    fun fetchProductById(productId: Long): ProductRow? =
        ProductTable
            .selectAll()
            .where { (ProductTable.id eq productId) and (ProductTable.isDelete eq false) }
            .singleOrNull()
            ?.let(ProductRow::from)

//    fun insertProduct(
//        crawledProduct: CrawledProduct
//    ): Long =
//        ProductTable.insertAudited {
//            it[ProductTable.store]          = Store.valueOf(crawledProduct.store)
//            it[ProductTable.storeNumber]    = crawledProduct.storeNumber
//            it[ProductTable.name]           = crawledProduct.name
//            it[ProductTable.brand]          = crawledProduct.brand
//            it[ProductTable.thumbnailUrl]   = crawledProduct.thumbnailUrl
//            it[ProductTable.price]          = crawledProduct.price
//            it[ProductTable.firstCategory]  = crawledProduct.firstCategory
//            it[ProductTable.secondCategory] = crawledProduct.secondCategory
//            it[ProductTable.firstOption]    = crawledProduct.firstOption
//            it[ProductTable.secondOption]   = crawledProduct.secondOption
//            it[ProductTable.thirdOption]    = crawledProduct.thirdOption
//        }[ProductTable.id].value

    /**
         * Partially updates a non-deleted product with values from a CrawledProduct.
         *
         * Updates the product identified by [existingProductId] only if it is not marked deleted
         * (isDelete = false). Fields updated: name, brand, thumbnailUrl, price, firstCategory, secondCategory.
         *
         * @param existingProductId ID of the product to update.
         * @param crawledProduct Source object providing the new field values.
         * @return The number of rows affected (should be 0 or 1).
         */
    fun updateProduct(
        existingProductId: Long,
        crawledProduct: CrawledProduct
    ): Int =
        ProductTable.update({ (ProductTable.id eq existingProductId) and (ProductTable.isDelete eq false) }) {
            it[ProductTable.name]          = crawledProduct.name
            it[ProductTable.brand]         = crawledProduct.brand
            it[ProductTable.thumbnailUrl]  = crawledProduct.thumbnailUrl
            it[ProductTable.price]         = crawledProduct.price
            it[ProductTable.firstCategory] = crawledProduct.firstCategory
            it[ProductTable.secondCategory]= crawledProduct.secondCategory
        }

    /**
         * Compares a nullable string column to a nullable value, producing an Exposed boolean expression.
         *
         * If `value` is null the resulting predicate checks the column IS NULL; otherwise it checks column = `value`.
         *
         * @receiver The nullable string column to compare.
         * @param value The nullable value to compare against.
         * @return An `Op<Boolean>` predicate representing the comparison.
         */
        private fun Column<String?>.eqNullable(value: String?): Op<Boolean> =
        if (value == null) this.isNull() else (this eq value)
}