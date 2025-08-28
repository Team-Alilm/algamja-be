package org.team_alilm.notification.repository

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.team_alilm.common.entity.updateAudited
import org.team_alilm.notification.entity.NotificationRow
import org.team_alilm.notification.entity.NotificationTable

@Repository
class NotificationExposedRepository {

    /** countByMemberIdAndReadYnFalse */
    fun countUnreadByMemberId(memberId: Long): Long {
        val cnt = NotificationTable.id.count()
        return NotificationTable
            .select(cnt)
            .where {
                (NotificationTable.memberId eq memberId) and
                        (NotificationTable.readYn eq false) and
                        (NotificationTable.isDelete eq false)
            }
            .firstOrNull()
            ?.get(cnt)
            ?: 0L
    }

    /** findAllByMemberIdAndReadYnIsFalseAndCreatedDateAfter */
    fun fetchUnreadByMemberIdCreatedAfter(
        memberId: Long,
        createdDateExclusive: Long
    ): List<NotificationRow> =
        NotificationTable
            .selectAll()
            .where {
                (NotificationTable.memberId eq memberId) and
                        (NotificationTable.readYn eq false) and
                        (NotificationTable.isDelete eq false) and
                        (NotificationTable.createdDate greater createdDateExclusive)
            }
            .orderBy(NotificationTable.id to SortOrder.DESC)
            .map(NotificationRow::from)

    /** 추가: 단건 읽음 처리 (소유자 조건으로 업데이트, 영향 행 수 반환) */
    fun markReadByIdAndMemberId(notificationId: Long, memberId: Long): Int =
        NotificationTable.updateAudited(
            where = {
                (NotificationTable.id eq notificationId) and
                        (NotificationTable.memberId eq memberId) and
                        (NotificationTable.readYn eq false) and
                        (NotificationTable.isDelete eq false)
            }
        ) {
            it[readYn] = true
        }

    /** 추가: 회원의 ‘읽지 않은’ 알림 전체 읽음 처리 (영향 행 수 반환) */
    fun markAllUnreadReadByMemberId(memberId: Long): Int =
        NotificationTable.updateAudited(
            where = {
                (NotificationTable.memberId eq memberId) and
                        (NotificationTable.readYn eq false) and
                        (NotificationTable.isDelete eq false)
            }
        ) {
            it[readYn] = true
        }
}