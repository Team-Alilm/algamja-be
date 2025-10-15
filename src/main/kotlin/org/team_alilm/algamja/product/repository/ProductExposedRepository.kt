package org.team_alilm.algamja.product.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.springframework.stereotype.Repository
import org.team_alilm.algamja.basket.entity.BasketTable
import org.team_alilm.algamja.common.entity.updateAudited
import org.team_alilm.algamja.common.enums.Sort.*
import org.team_alilm.algamja.product.controller.v1.dto.param.ProductListParam
import org.team_alilm.algamja.product.controller.v1.dto.param.ProductCountParam
import org.team_alilm.algamja.product.entity.ProductRow
import org.team_alilm.algamja.product.entity.ProductTable
import org.team_alilm.algamja.product.repository.projection.ProductSliceProjection
import org.team_alilm.algamja.common.enums.Store
import java.math.BigDecimal

@Repository
class ProductExposedRepository {

    fun markProductAsPurchased(productId: Long) {
        ProductTable.updateAudited(
            where = { ProductTable.id eq productId }
        ) {
            it[isAvailable] = false
            it[lastCheckedAt] = System.currentTimeMillis()
        }
    }
    
    fun updateProductAvailability(productId: Long, available: Boolean) {
        ProductTable.updateAudited(
            where = { ProductTable.id eq productId }
        ) {
            it[isAvailable] = available
            it[lastCheckedAt] = System.currentTimeMillis()
        }
    }

    /** 공통 WHERE 빌더 (목록/카운트에서 재사용) */
    private fun buildBaseWhere(param: ProductListParam): Op<Boolean> {
        val table = ProductTable
        val like = param.keyword?.trim()?.takeIf { it.isNotEmpty() }?.let { "%$it%" }
        val categoryKey = param.category?.trim()?.takeIf { it.isNotEmpty() }

        return listOfNotNull(
            table.isDelete eq false,
            like?.let { (table.name like it) or (table.brand like it) },
            // 이제 카테고리가 영어로 저장되므로 직접 비교
            categoryKey?.let {
                (table.firstCategory eq it) or (table.secondCategory eq it)
            }
        ).fold(initial = Op.TRUE as Op<Boolean>) { acc, op -> acc and op }
    }

    /** ProductCountParam용 WHERE 빌더 */
    private fun buildCountWhere(param: ProductCountParam): Op<Boolean> {
        var predicate: Op<Boolean> = ProductTable.isDelete eq false

        // 키워드 검색 조건 추가
        param.keyword?.trim()?.takeIf { it.isNotEmpty() }?.let { keyword ->
            val searchPattern = "%$keyword%"
            predicate = predicate and ((ProductTable.name like searchPattern) or (ProductTable.brand like searchPattern))
        }

        // 카테고리 검색 조건 추가
        param.category?.trim()?.takeIf { it.isNotEmpty() }?.let { category ->
            predicate = predicate and ((ProductTable.firstCategory eq category) or (ProductTable.secondCategory eq category))
        }

        return predicate
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

        // 커서 처리: 마지막 대기자수와 상품 ID로 페이징 (NULL 처리 포함)
        val cursorCondition = if (param.lastWaitingCount != null && param.lastProductId != null) {
            val lastWaitingCount = param.lastWaitingCount
            if (lastWaitingCount > 0) {
                // 대기자수가 0보다 크면 NULL인 경우(대기자 0명)도 포함
                (waitingCol less lastWaitingCount) or 
                ((waitingCol eq lastWaitingCount) and (table.id less param.lastProductId))
            } else {
                // 대기자수가 0이면 ID로만 비교
                table.id less param.lastProductId
            }
        } else null

        val finalWhere = cursorCondition?.let { baseWhere and it } ?: baseWhere

        val rows = table
            .join(basketAgg, JoinType.LEFT, additionalConstraint = { table.id eq aggPidCol })
            .selectAll()
            .where { finalWhere }
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

    /** 같은 필터로 '총 개수' 조회 */
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

    /** 검색 조건에 따른 '총 개수' 조회 (정렬 불필요) */
    fun countProducts(param: ProductCountParam): Long {
        val predicate = buildCountWhere(param)
        val count = ProductTable.id.count()

        return ProductTable
            .select(count)
            .where(predicate)
            .single()[count]
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

    /** 스토어 번호와 옵션으로 상품 조회 (중복 등록 방지용) */
    fun fetchProductByStoreNumber(
        storeNumber: Long, 
        store: Store,
        firstOption: String?,
        secondOption: String?,
        thirdOption: String?
    ): ProductRow? =
        ProductTable
            .selectAll()
            .where { 
                val conditions = mutableListOf<Op<Boolean>>(
                    ProductTable.storeNumber eq storeNumber,
                    ProductTable.store eq store,
                    ProductTable.isDelete eq false
                )
                
                // firstOption 비교 (빈 문자열과 null 처리)
                conditions.add(
                    if (firstOption.isNullOrEmpty()) 
                        ProductTable.firstOption eq ""
                    else 
                        ProductTable.firstOption eq firstOption
                )
                
                // secondOption 비교
                conditions.add(
                    if (secondOption == null) 
                        ProductTable.secondOption.isNull()
                    else 
                        ProductTable.secondOption eq secondOption
                )
                
                // thirdOption 비교
                conditions.add(
                    if (thirdOption == null) 
                        ProductTable.thirdOption.isNull()
                    else 
                        ProductTable.thirdOption eq thirdOption
                )
                
                conditions.reduce { acc, op -> acc and op }
            }
            .singleOrNull()
            ?.let(ProductRow::from)

    /** 새 상품 등록 (리스트 옵션) - 스토어별 옵션 처리 */
    fun save(
        name: String,
        storeNumber: Long,
        brand: String,
        thumbnailUrl: String,
        store: Store,
        price: BigDecimal,
        firstCategory: String,
        secondCategory: String?,
        firstOptions: List<String>,
        secondOptions: List<String>,
        thirdOptions: List<String>
    ): ProductRow {
        val now = System.currentTimeMillis()
        
        // 스토어별 옵션 처리 전략 결정
        val optionCombinations = when (store) {
            Store.MUSINSA -> generateMusinsaOptionCombinations(firstOptions, secondOptions, thirdOptions)
            else -> generateDefaultOptionCombinations(firstOptions, secondOptions, thirdOptions)
        }
        
        var savedProduct: ProductRow? = null
        
        // 각 옵션 조합마다 상품 레코드 생성
        optionCombinations.forEach { (first, second, third) ->
            val insertedId = ProductTable.insertAndGetId { row ->
                row[ProductTable.storeNumber] = storeNumber
                row[ProductTable.name] = name
                row[ProductTable.brand] = brand
                row[ProductTable.thumbnailUrl] = thumbnailUrl
                row[ProductTable.store] = store
                row[ProductTable.price] = price
                row[ProductTable.firstCategory] = firstCategory
                row[ProductTable.secondCategory] = secondCategory
                row[ProductTable.firstOption] = first?.take(120) ?: ""
                row[ProductTable.secondOption] = second?.take(120)
                row[ProductTable.thirdOption] = third?.take(120)
                row[ProductTable.createdAt] = now
                row[ProductTable.updatedAt] = now
                row[ProductTable.isDelete] = false
            }
            
            // 첫 번째 조합의 상품을 반환용으로 저장
            if (savedProduct == null) {
                savedProduct = fetchProductById(insertedId.value)
            }
        }
        
        return savedProduct ?: throw IllegalStateException("Failed to save any product combinations")
    }
    
    /** 가격 업데이트용 상품 배치 조회 (페이징) */
    fun fetchProductsForPriceUpdateBatch(batchSize: Int, offset: Int): List<ProductRow> {
        return ProductTable
            .selectAll()
            .where { ProductTable.isDelete eq false }
            .orderBy(ProductTable.id)
            .limit(count = batchSize)
            .offset(offset.toLong())
            .map(ProductRow::from)
    }
    
    /** 상품 가격 업데이트 */
    fun updatePrice(productId: Long, newPrice: BigDecimal): Int {
        return ProductTable
            .updateAudited({ ProductTable.id eq productId }) { row ->
                row[ProductTable.price] = newPrice
            }
    }
    
    /** 무신사용 옵션 조합 생성 - 전체 조합 생성 */
    private fun generateMusinsaOptionCombinations(
        firstOptions: List<String>,
        secondOptions: List<String>,
        thirdOptions: List<String>
    ): List<Triple<String?, String?, String?>> {
        val first = firstOptions.ifEmpty { listOf(null) }
        val second = secondOptions.ifEmpty { listOf(null) }
        val third = thirdOptions.ifEmpty { listOf(null) }
        
        val combinations = mutableListOf<Triple<String?, String?, String?>>()
        for (f in first) {
            for (s in second) {
                for (t in third) {
                    combinations.add(Triple(f, s, t))
                }
            }
        }
        
        return combinations
    }
    
    /** 기본 옵션 조합 생성 - 전체 조합 생성 */
    private fun generateDefaultOptionCombinations(
        firstOptions: List<String>,
        secondOptions: List<String>,
        thirdOptions: List<String>
    ): List<Triple<String?, String?, String?>> {
        return generateMusinsaOptionCombinations(firstOptions, secondOptions, thirdOptions)
    }

    /** 새 상품 등록 (단일 옵션) */
    fun save(
        name: String,
        storeNumber: Long,
        brand: String,
        thumbnailUrl: String,
        store: Store,
        price: BigDecimal,
        firstCategory: String,
        secondCategory: String?,
        firstOption: String,
        secondOption: String?,
        thirdOption: String?
    ): ProductRow {
        val now = System.currentTimeMillis()
        val insertedId = ProductTable.insertAndGetId { row ->
            row[ProductTable.storeNumber] = storeNumber
            row[ProductTable.name] = name
            row[ProductTable.brand] = brand
            row[ProductTable.thumbnailUrl] = thumbnailUrl
            row[ProductTable.store] = store
            row[ProductTable.price] = price
            row[ProductTable.firstCategory] = firstCategory
            row[ProductTable.secondCategory] = secondCategory
            row[ProductTable.firstOption] = firstOption.take(120)
            row[ProductTable.secondOption] = secondOption?.take(120)
            row[ProductTable.thirdOption] = thirdOption?.take(120)
            row[ProductTable.createdAt] = now
            row[ProductTable.updatedAt] = now
            row[ProductTable.isDelete] = false
        }
        
        return fetchProductById(insertedId.value)
            ?: throw IllegalStateException("Failed to retrieve saved product with ID: ${insertedId.value}")
    }

    /** 모든 활성 상품 조회 (스케줄러용) */
    fun fetchAllActiveProducts(): List<ProductRow> =
        ProductTable
            .selectAll()
            .where { ProductTable.isDelete eq false }
            .map(ProductRow::from)
    
}