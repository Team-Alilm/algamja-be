package org.team_alilm.fcm.entity

import org.jetbrains.exposed.sql.ResultRow

data class FcmTokenRow(
    val id: Long,
    val token: String,
    val memberId: Long,
    val isActive: Boolean,
    val isDelete: Boolean,
    val createdDate: Long,
    val lastModifiedDate: Long
) {
    companion object {
        fun from(row: ResultRow): FcmTokenRow = FcmTokenRow(
            id              = row[FcmTokenTable.id].value,
            token           = row[FcmTokenTable.token],
            memberId        = row[FcmTokenTable.memberId],
            isActive        = row[FcmTokenTable.isActive],
            isDelete        = row[FcmTokenTable.isDelete],
            createdDate     = row[FcmTokenTable.createdDate],
            lastModifiedDate= row[FcmTokenTable.lastModifiedDate],
        )
    }
}