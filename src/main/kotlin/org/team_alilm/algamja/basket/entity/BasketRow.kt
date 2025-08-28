package org.team_alilm.algamja.basket.entity

import org.jetbrains.exposed.sql.ResultRow

data class BasketRow(
    val id: Long,
    val memberId: Long,
    val productId: Long,
    val isNotification: Boolean,
    val notificationDate: Long?,
    val isHidden: Boolean,
    val isDelete: Boolean,
    val createdDate: Long,
    val lastModifiedDate: Long,
) {
    companion object {
        fun from(row: ResultRow) = BasketRow(
            id = row[BasketTable.id].value,
            memberId = row[BasketTable.memberId],
            productId = row[BasketTable.productId],
            isNotification = row[BasketTable.isNotification],
            notificationDate = row[BasketTable.notificationDate],
            isHidden = row[BasketTable.isHidden],
            isDelete = row[BasketTable.isDelete],
            createdDate = row[BasketTable.createdDate],
            lastModifiedDate = row[BasketTable.lastModifiedDate],
        )
    }
}