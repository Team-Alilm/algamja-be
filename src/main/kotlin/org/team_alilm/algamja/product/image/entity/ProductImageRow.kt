package org.team_alilm.algamja.product.image.entity

import org.jetbrains.exposed.sql.ResultRow

data class ProductImageRow(
    val id: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val isDelete: Boolean,
    val imageUrl: String,
    val productId: Long
) {
    companion object {
        fun from(row: ResultRow): ProductImageRow =
            ProductImageRow(
                id = row[ProductImageTable.id].value,
                createdAt = row[ProductImageTable.createdAt],
                updatedAt = row[ProductImageTable.updatedAt],
                isDelete = row[ProductImageTable.isDelete],
                imageUrl = row[ProductImageTable.imageUrl],
                productId = row[ProductImageTable.productId]
            )
    }
}