package org.team_alilm.product.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import org.team_alilm.product.controller.dto.response.ProductDetailResponse
import org.team_alilm.product.entity.ProductTable
import org.team_alilm.product.image.entity.ProductImageTable
import org.team_alilm.basket.exposed.BasketTable
import org.team_alilm.common.enums.Sort
import org.team_alilm.product.controller.dto.param.ProductListParam
import org.team_alilm.product.controller.dto.response.ProductListResponse
import org.team_alilm.product.controller.dto.response.ProductResponse
import org.team_alilm.product.controller.dto.response.SimilarProductListResponse
import org.team_alilm.product.controller.dto.response.SimilarProductResponse
import java.math.BigDecimal

@Repository
class ProductQueryRepository {

    fun findProductDetail(productId: Long): ProductDetailResponse? = transaction {
        // 1) 상품 기본 정보 (LIMIT 1)
        val base = ProductTable
            .select(
                ProductTable.id,
                ProductTable.storeNumber,
                ProductTable.name,
                ProductTable.brand,
                ProductTable.thumbnailUrl,
                ProductTable.store,
                ProductTable.price,
                ProductTable.firstOption,
                ProductTable.secondOption,
                ProductTable.thirdOption
            )
            .where { ProductTable.id eq productId }
            .limit(1)
            .firstOrNull() ?: return@transaction null

        // 2) 이미지 목록 (정렬 보장)
        val images = ProductImageTable
            .select(ProductImageTable.imageUrl)
            .where { ProductImageTable.productId eq productId }
            .orderBy(ProductImageTable.id to SortOrder.ASC)
            .mapNotNull { it[ProductImageTable.imageUrl] }
            .distinct()

        // 3) 장바구니 대기 수 (is_notification = false)
        val cnt = BasketTable.id.count()
        val waitingCount = BasketTable
            .select(cnt)
            .where {
                (BasketTable.productId eq productId) and
                        (BasketTable.isNotification eq false) and
                        (BasketTable.isHidden eq false) and
                        (BasketTable.isDelete eq false)
            }
            .firstOrNull()
            ?.get(cnt) ?: 0L

        ProductDetailResponse(
            id = base[ProductTable.id],
            number = base[ProductTable.storeNumber],
            name = base[ProductTable.name],
            brand = base[ProductTable.brand],
            thumbnailUrl = base[ProductTable.thumbnailUrl],
            imageUrlList = images,
            store = base[ProductTable.store],
            price = base[ProductTable.price].toLong(),
            firstOption = base[ProductTable.firstOption],
            secondOption = base[ProductTable.secondOption],
            thirdOption = base[ProductTable.thirdOption],
            waitingCount = waitingCount
        )
    }

    fun findProductsByCursor(param: ProductListParam): ProductListResponse = transaction {
        val size = (param.size.takeIf { it > 0 } ?: 20).coerceAtMost(100)

        val where = buildBaseWhere(param) and buildCursorCond(param)

        // WAITING_COUNT_DESC는 별도 분기
        if (param.sort == Sort.WAITING_COUNT_DESC) {
            return@transaction queryByWaitingCount(where, param, size)
        }

        val rows = orderedBaseQuery(where, param.sort)
            .limit(size + 1)
            .toList()

        if (rows.isEmpty()) return@transaction ProductListResponse(emptyList(), false)

        val hasNext = rows.size > size
        val pageRows = if (hasNext) rows.dropLast(1) else rows
        val waitingMap = fetchWaitingCounts(pageRows.map { it[ProductTable.id] })

        val filtered = param.lastWaitingCount?.let { min ->
            pageRows.filter { (waitingMap[it[ProductTable.id]] ?: 0L) >= min }
        } ?: pageRows

        ProductListResponse(mapProducts(filtered, waitingMap), hasNext)
    }

    /* -------------------- helpers -------------------- */

    private fun buildBaseWhere(param: ProductListParam): Op<Boolean> {
        var where: Op<Boolean> = Op.TRUE
        if (!param.keyword.isNullOrBlank()) {
            val kw = "%${param.keyword.trim()}%"
            where = where and ((ProductTable.name like kw) or (ProductTable.brand like kw))
        }
        if (!param.category.isNullOrBlank()) {
            val cat = param.category
            where = where and ((ProductTable.firstCategory eq cat) or (ProductTable.secondCategory eq cat))
        }
        return where
    }

    private fun buildCursorCond(param: ProductListParam): Op<Boolean> = with(SqlExpressionBuilder) {
        when (param.sort) {
            Sort.PRICE_ASC  -> priceCursorAsc(param.lastPrice, param.lastProductId)
            Sort.PRICE_DESC -> priceCursorDesc(param.lastPrice, param.lastProductId)
            Sort.CREATED_DATE_DESC -> param.lastProductId?.let { ProductTable.id less it } ?: Op.TRUE
            Sort.WAITING_COUNT_DESC -> Op.TRUE // 별도 분기에서 처리
        }
    }

    private fun priceCursorAsc(lastPrice: Int?, lastId: Long?): Op<Boolean> = with(SqlExpressionBuilder) {
        val lp: BigDecimal? = lastPrice?.toBigDecimal()
        if (lp == null || lastId == null) Op.TRUE
        else (ProductTable.price greater lp) or
                ((ProductTable.price eq lp) and (ProductTable.id less lastId))
    }

    private fun priceCursorDesc(lastPrice: Int?, lastId: Long?): Op<Boolean> = with(SqlExpressionBuilder) {
        val lp: BigDecimal? = lastPrice?.toBigDecimal()
        if (lp == null || lastId == null) Op.TRUE
        else (ProductTable.price less lp) or
                ((ProductTable.price eq lp) and (ProductTable.id less lastId))
    }

    private fun baseSelect(where: Op<Boolean>): Query =
        ProductTable
            .select(
                ProductTable.id,
                ProductTable.storeNumber,
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
            )
            .where { where }

    private fun orderedBaseQuery(where: Op<Boolean>, sort: Sort): Query =
        when (sort) {
            Sort.PRICE_ASC  -> baseSelect(where)
                .orderBy(ProductTable.price to SortOrder.ASC, ProductTable.id to SortOrder.DESC)
            Sort.PRICE_DESC -> baseSelect(where)
                .orderBy(ProductTable.price to SortOrder.DESC, ProductTable.id to SortOrder.DESC)
            Sort.CREATED_DATE_DESC,
            Sort.WAITING_COUNT_DESC -> baseSelect(where)
                .orderBy(ProductTable.id to SortOrder.DESC)
        }

    private fun fetchWaitingCounts(ids: List<Long>): Map<Long, Long> {
        if (ids.isEmpty()) return emptyMap()
        val cnt = BasketTable.id.count().alias("waiting_count")
        return BasketTable
            .select(BasketTable.productId, cnt)
            .where { (BasketTable.productId inList ids) and (BasketTable.isNotification eq false) }
            .groupBy(BasketTable.productId)
            .associate { it[BasketTable.productId] to it[cnt] }
    }

    private fun mapProducts(rows: List<ResultRow>, waiting: Map<Long, Long>): List<ProductResponse> =
        rows.map { r ->
            ProductResponse(
                id = r[ProductTable.id],
                name = r[ProductTable.name],
                brand = r[ProductTable.brand],
                thumbnailUrl = r[ProductTable.thumbnailUrl],
                store = r[ProductTable.store],
                price = r[ProductTable.price].toLong(), // 필요 시 DTO를 Long로
                firstCategory = r[ProductTable.firstCategory],
                secondCategory = r[ProductTable.secondCategory],
                firstOption = r[ProductTable.firstOption],
                secondOption = r[ProductTable.secondOption],
                thirdOption = r[ProductTable.thirdOption],
                waitingCount = waiting[r[ProductTable.id]] ?: 0L
            )
        }

    private fun queryByWaitingCount(
        where: Op<Boolean>,
        param: ProductListParam,
        size: Int
    ): ProductListResponse {
        val waitingCnt = BasketTable.id.count().alias("waiting_count")
        val basketAgg = BasketTable
            .select(BasketTable.productId, waitingCnt)
            .where { BasketTable.isNotification eq false }
            .groupBy(BasketTable.productId)
            .alias("basket_agg")

        val wcCursor: Op<Boolean> = with(SqlExpressionBuilder) {
            val lastWC = param.lastWaitingCount
            val lastPid = param.lastProductId
            if (lastWC == null || lastPid == null) Op.TRUE
            else {
                val wc = basketAgg[waitingCnt]
                (wc less lastWC) or ((wc eq lastWC) and (ProductTable.id less lastPid))
            }
        }

        val rows = ProductTable
            .join(basketAgg, JoinType.LEFT, ProductTable.id, basketAgg[BasketTable.productId])
            .select(
                ProductTable.id,
                ProductTable.storeNumber,
                ProductTable.name,
                ProductTable.brand,
                ProductTable.thumbnailUrl,
                ProductTable.store,
                ProductTable.price,
                ProductTable.firstCategory,
                ProductTable.secondCategory,
                ProductTable.firstOption,
                ProductTable.secondOption,
                ProductTable.thirdOption,
                basketAgg[waitingCnt]
            )
            .where { where and wcCursor }
            .orderBy(basketAgg[waitingCnt] to SortOrder.DESC, ProductTable.id to SortOrder.DESC)
            .limit(size + 1)
            .toList()

        if (rows.isEmpty()) return ProductListResponse(emptyList(), false)

        val hasNext = rows.size > size
        val page = if (hasNext) rows.dropLast(1) else rows
        val ids = page.map { it[ProductTable.id] }
        val waitingMap = fetchWaitingCounts(ids)

        return ProductListResponse(mapProducts(page, waitingMap), hasNext)
    }

    fun getSimilarProducts(productId: Long): SimilarProductListResponse = transaction {
        // 1) 기준 상품을 base라는 alias로 생성
        val base = ProductTable.alias("base")

        // 2) ProductTable과 기준 상품(base)을 self join
        val rows = ProductTable
            .join(
                otherTable = base,
                joinType = JoinType.INNER,
                // 기준 상품 한 건만 선택 (base.id = productId)
                additionalConstraint = { base[ProductTable.id] eq productId }
            )
            .select(
                ProductTable.id,
                ProductTable.name,
                ProductTable.brand,
                ProductTable.thumbnailUrl
            )
            .where {
                // 자기 자신은 제외
                (ProductTable.id neq productId) and (
                        // 1차 카테고리가 같거나,
                        (ProductTable.firstCategory eq base[ProductTable.firstCategory]) or
                                // 2차 카테고리가 존재하고, 2차 카테고리도 같을 때
                                (base[ProductTable.secondCategory].isNotNull() and
                                        (ProductTable.secondCategory eq base[ProductTable.secondCategory]))
                        )
            }
            // id 내림차순 정렬
            .orderBy(ProductTable.id to SortOrder.DESC)
            // 최대 10개까지
            .limit(10)
            // DTO 매핑
            .map {
                SimilarProductResponse(
                    productId = it[ProductTable.id],
                    name = it[ProductTable.name],
                    brand = it[ProductTable.brand],
                    thumbnailUrl = it[ProductTable.thumbnailUrl]
                )
            }

        // 3) 응답 래핑
        SimilarProductListResponse(rows)
    }
}