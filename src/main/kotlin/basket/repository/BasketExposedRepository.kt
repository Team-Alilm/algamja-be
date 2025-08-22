package org.team_alilm.basket.repository

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.count
import org.springframework.stereotype.Repository
import org.team_alilm.basket.entity.BasketRow
import org.team_alilm.basket.entity.BasketTable
import org.jetbrains.exposed.sql.SortOrder.DESC
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.selectAll
import org.team_alilm.common.entity.insertAudited
import org.team_alilm.common.entity.updateAudited
import org.team_alilm.product.repository.projection.ProductWaitingCountProjection

@Repository
class BasketExposedRepository {

    fun fetchBasketById(basketId: Long): BasketRow? =
        BasketTable
            .selectAll()
            .where { (BasketTable.id eq basketId) and (BasketTable.isDelete eq false) }
            .map(BasketRow::from)
            .firstOrNull()

    /** 1) 회원의 장바구니 행만 조회 */
    fun fetchBasketsByMemberId(memberId: Long): List<BasketRow> =
        BasketTable
            .selectAll()
            .where {
                (BasketTable.memberId eq memberId) and
                        (BasketTable.isDelete eq false) and
                        (BasketTable.isHidden eq false)
            }
            .orderBy(BasketTable.id to DESC)
            .map(BasketRow::from)

    fun fetchBasketByMemberIdAndProductId(
        memberId: Long,
        productId: Long
    ): BasketRow? =
        BasketTable
            .selectAll()
            .where {
                (BasketTable.memberId eq memberId) and
                (BasketTable.productId eq productId) and
                (BasketTable.isDelete eq false)
            }
            .limit(1)
            .firstOrNull()
            ?.let(BasketRow::from)

    fun fetchAnyBasketByMemberIdAndProductId(
        memberId: Long,
        productId: Long
    ): BasketRow? =
        BasketTable
            .selectAll()
            .where {
                (BasketTable.memberId eq memberId) and
                        (BasketTable.productId eq productId)
            }
            .limit(1)
            .firstOrNull()
            ?.let(BasketRow::from)

    /** 2) productId별 대기 인원 수 집계 (다건) */
    fun fetchWaitingCounts(productIds: List<Long>): List<ProductWaitingCountProjection> =
        aggregateWaitingCounts(productIds)

    /** 3) 단일 productId의 대기 인원 수 집계 (단건) */
    fun fetchWaitingCount(productId: Long): Long =
        aggregateWaitingCounts(listOf(productId)).firstOrNull()?.waitingCount
            ?: 0L // 없으면 0 반환

    // ---------- 공통 헬퍼 ----------

    /**
     * 공통 집계 로직:
     * - 중복 제거
     * - 대용량 IN 대비 청크 처리
     * - where + groupBy + 매핑
     */
    private fun aggregateWaitingCounts(productIds: List<Long>): List<ProductWaitingCountProjection> {
        val ids = productIds.distinct()
        if (ids.isEmpty()) return emptyList()

        val waitingCnt = BasketTable.id.count()
        val chunkSize = 1000

        return ids.chunked(chunkSize).flatMap { chunk ->
            BasketTable
                .select(
                    listOf<Expression<*>>(
                        BasketTable.productId,
                        waitingCnt
                    )
                )
                .where {
                    (BasketTable.productId inList chunk) and
                            (BasketTable.isNotification eq false) and
                            (BasketTable.isHidden eq false) and
                            (BasketTable.isDelete eq false)
                }
                .groupBy(BasketTable.productId)
                .map { row ->
                    ProductWaitingCountProjection(
                        productId = row[BasketTable.productId],
                        waitingCount = row[waitingCnt]
                    )
                }
        }
    }

    fun createBasket(memberId: Long, productId: Long): Long {
        val stmt = BasketTable.insertAudited {
            it[this.memberId]         = memberId
            it[this.productId]        = productId
            it[this.isNotification]   = false
            it[this.notificationDate] = null
            it[this.isHidden]         = false
            it[this.isDelete]         = false
        }
        return stmt[BasketTable.id].value
    }

    fun deleteBasket(basketId: Long) {
        BasketTable.updateAudited(
            where = {
                (BasketTable.id eq basketId) and
                        (BasketTable.isDelete eq false)
            }
        ) {
            it[isDelete] = true
        }
    }

    fun restoreBasket(
        basketId: Long
    ) {
        BasketTable.updateAudited(
            where = {
                (BasketTable.id eq basketId)
            }
        ) {
            it[isDelete] = false
            it[isHidden] = false
        }
    }

    fun fetchTop10RecentlyNotifiedProductIds(): List<Long> {
        val maxId = BasketTable.id.max().alias("max_id")

        return BasketTable
            .select(BasketTable.productId, maxId)   // ← slice 대신 select(vararg)
            .where {
                (BasketTable.isNotification eq true) and
                (BasketTable.isDelete eq false) and
                (BasketTable.isHidden eq false) and
                (BasketTable.isNotification eq true)
            }
            .groupBy(BasketTable.productId)
            .orderBy(maxId to DESC)
            .limit(10)
            .map { it[BasketTable.productId] }
    }
}