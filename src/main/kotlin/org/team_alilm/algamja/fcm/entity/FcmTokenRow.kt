package org.team_alilm.algamja.fcm.entity

import org.jetbrains.exposed.sql.ResultRow

data class FcmTokenRow(
    val id: Long,
    val token: String,
    val memberId: Long,
    val isActive: Boolean,
    val isDelete: Boolean,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        fun from(row: ResultRow): FcmTokenRow = FcmTokenRow(
            id              = row[FcmTokenTable.id].value,
            token           = row[FcmTokenTable.token],
            memberId        = row[FcmTokenTable.memberId],
            isActive        = row[FcmTokenTable.isActive],
            isDelete        = row[FcmTokenTable.isDelete],
            createdAt     = row[FcmTokenTable.createdAt],
            updatedAt= row[FcmTokenTable.updatedAt],
        )
    }
}