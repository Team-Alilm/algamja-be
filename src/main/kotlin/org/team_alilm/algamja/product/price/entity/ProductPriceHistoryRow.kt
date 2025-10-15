package org.team_alilm.algamja.product.price.entity

import org.jetbrains.exposed.sql.ResultRow
import java.math.BigDecimal

data class ProductPriceHistoryRow(
    val id: Long,
    val productId: Long,
    val price: BigDecimal,
    val recordedAt: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val isDelete: Boolean
) {
    companion object {
        fun from(row: ResultRow): ProductPriceHistoryRow {
            return ProductPriceHistoryRow(
                id = row[ProductPriceHistoryTable.id].value,
                productId = row[ProductPriceHistoryTable.productId],
                price = row[ProductPriceHistoryTable.price],
                recordedAt = row[ProductPriceHistoryTable.recordedAt],
                createdAt = row[ProductPriceHistoryTable.createdAt],
                updatedAt = row[ProductPriceHistoryTable.updatedAt],
                isDelete = row[ProductPriceHistoryTable.isDelete]
            )
        }
    }
}