package org.team_alilm.basket.repository

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.count
import org.springframework.stereotype.Repository
import org.team_alilm.basket.entity.BasketRow
import org.team_alilm.basket.entity.BasketTable
import org.jetbrains.exposed.sql.SortOrder.DESC
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.selectAll
import org.team_alilm.common.entity.insertAudited
import org.team_alilm.product.repository.projection.ProductWaitingCountProjection

@Repository
class BasketExposedRepository {

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

    fun createBasket(
        memberId: Long,
        productId: Long
    ) : Long {
        val now = System.currentTimeMillis()
        val stmt = BasketTable.insertAudited {
            it[memberId] = memberId
            it[productId] = productId
            it[isNotification] = false
            it[notificationDate] = null
            it[isHidden] = false
            it[isDelete] = false
            it[createdDate] = now
            it[lastModifiedDate] = now
        }
    }
}