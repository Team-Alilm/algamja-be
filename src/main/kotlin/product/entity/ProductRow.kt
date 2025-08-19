package org.team_alilm.product.entity

import org.jetbrains.exposed.sql.ResultRow
import org.team_alilm.common.enums.Store
import java.math.BigDecimal

data class ProductRow(
    val id: Long,
    val storeNumber: Long,
    val name: String,
    val brand: String,
    val thumbnailUrl: String,
    val store: Store,
    val firstCategory: String,
    val secondCategory: String?,
    val price: BigDecimal,
    val firstOption: String?,
    val secondOption: String?,
    val thirdOption: String?,

    // audit
    val isDelete: Boolean,
    val createdDate: Long,
    val updatedDate: Long,
) {
    companion object {
        fun from(row: ResultRow) = ProductRow(
            id            = row[ProductTable.id].value,   // EntityID<Long> → Long
            storeNumber   = row[ProductTable.storeNumber],
            name          = row[ProductTable.name],
            brand         = row[ProductTable.brand],
            thumbnailUrl  = row[ProductTable.thumbnailUrl],
            store         = row[ProductTable.store],
            firstCategory = row[ProductTable.firstCategory],
            secondCategory= row[ProductTable.secondCategory],
            price         = row[ProductTable.price],
            firstOption   = row[ProductTable.firstOption],
            secondOption  = row[ProductTable.secondOption],
            thirdOption   = row[ProductTable.thirdOption],
            isDelete      = row[ProductTable.isDelete],
            createdDate   = row[ProductTable.createdDate],
            updatedDate   = row[ProductTable.updatedDate],
        )
    }
}