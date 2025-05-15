package org.team_alilm.adapter.out.persistence.exposed.dao

import domain.product.Product
import domain.product.Store
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.team_alilm.adapter.out.persistence.exposed.table.ProductExposedTable

class ProductExposedEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ProductExposedEntity>(ProductExposedTable)

    var createdDate by ProductExposedTable.createdDate
    var lastModifiedDate by ProductExposedTable.lastModifiedDate
    var isDeleted by ProductExposedTable.isDeleted

    var brand by ProductExposedTable.brand
    var name by ProductExposedTable.name
    var number by ProductExposedTable.number
    var price by ProductExposedTable.price

    var firstCategory by ProductExposedTable.firstCategory
    var secondCategory by ProductExposedTable.secondCategory

    var firstOption by ProductExposedTable.firstOption
    var secondOption by ProductExposedTable.secondOption
    var thirdOption by ProductExposedTable.thirdOption

    var store by ProductExposedTable.store
    var thumbnailUrl by ProductExposedTable.thumbnailUrl

    fun toDomain(): Product {
        return Product(
            id = this.id.value,
            number = number,
            name = name,
            brand = brand,
            thumbnailUrl = thumbnailUrl,
            store = Store.valueOf(store),
            firstCategory = firstCategory,
            secondCategory = secondCategory,
            price = price,
            firstOption = firstOption,
            secondOption = secondOption,
            thirdOption = thirdOption,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}