package org.team_alilm.product.image.entity

import org.jetbrains.exposed.sql.ResultRow

data class ProductImageRow(
    val id: Long,
    val createdDate: Long,
    val lastModifiedDate: Long,
    val isDelete: Boolean,
    val imageUrl: String,
    val productId: Long
) {
    companion object {
        fun from(row: ResultRow): ProductImageRow =
            ProductImageRow(
                id = row[ProductImageTable.id].value,
                createdDate = row[ProductImageTable.createdDate],
                lastModifiedDate = row[ProductImageTable.lastModifiedDate],
                isDelete = row[ProductImageTable.isDelete],
                imageUrl = row[ProductImageTable.imageUrl],
                productId = row[ProductImageTable.productId]
            )
    }
}