package org.team_alilm.notification.entity

import org.jetbrains.exposed.sql.ResultRow

data class NotificationRow(
    val id: Long,
    val productId: Long,
    val memberId: Long,
    val readYn: Boolean,
    val isDelete: Boolean,
    val createdDate: Long,
    val lastModifiedDate: Long
) {
    companion object {
        fun from(row: ResultRow): NotificationRow =
            NotificationRow(
                id              = row[NotificationTable.id].value,
                productId       = row[NotificationTable.productId],
                memberId        = row[NotificationTable.memberId],
                readYn          = row[NotificationTable.readYn],
                isDelete        = row[NotificationTable.isDelete],
                createdDate     = row[NotificationTable.createdDate],
                lastModifiedDate= row[NotificationTable.lastModifiedDate],
            )
    }
}