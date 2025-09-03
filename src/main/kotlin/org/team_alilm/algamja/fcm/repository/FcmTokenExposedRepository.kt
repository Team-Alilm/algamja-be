package org.team_alilm.algamja.fcm.repository

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.team_alilm.algamja.common.entity.insertAudited
import org.team_alilm.algamja.fcm.entity.FcmTokenRow
import org.team_alilm.algamja.fcm.entity.FcmTokenTable

@Repository
class FcmTokenExposedRepository {

    fun fetchByFcmToken(fcmToken: String) : FcmTokenRow? =
        FcmTokenTable
            .selectAll()
            .where { (FcmTokenTable.token eq fcmToken) and (FcmTokenTable.isDelete eq false) }
            .limit(1)
            .firstOrNull()
            ?.let(FcmTokenRow::from)

    fun createFcmToken(memberId: Long, fcmToken: String) {
        FcmTokenTable.insertAudited {
            it[token] = fcmToken
            it[FcmTokenTable.memberId] = memberId
            it[isActive] = true
            it[isDelete] = false
        }
    }

    fun fetchLatestTokenByMemberId(memberId: Long): FcmTokenRow? =
        FcmTokenTable
            .selectAll()
            .where { 
                (FcmTokenTable.memberId eq memberId) and 
                (FcmTokenTable.isActive eq true) and 
                (FcmTokenTable.isDelete eq false) 
            }
            .orderBy(FcmTokenTable.createdDate to org.jetbrains.exposed.sql.SortOrder.DESC)
            .limit(1)
            .firstOrNull()
            ?.let(FcmTokenRow::from)
}