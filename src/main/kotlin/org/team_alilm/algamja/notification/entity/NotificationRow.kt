package org.team_alilm.algamja.notification.entity

import org.jetbrains.exposed.sql.ResultRow

data class NotificationRow(
    val id: Long,
    val productId: Long,
    val memberId: Long,
    val readYn: Boolean,
    val isDelete: Boolean,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        fun from(row: ResultRow): NotificationRow =
            NotificationRow(
                id              = row[NotificationTable.id].value,
                productId       = row[NotificationTable.productId],
                memberId        = row[NotificationTable.memberId],
                readYn          = row[NotificationTable.readYn],
                isDelete        = row[NotificationTable.isDelete],
                createdAt     = row[NotificationTable.createdAt],
                updatedAt= row[NotificationTable.updatedAt],
            )
    }
}