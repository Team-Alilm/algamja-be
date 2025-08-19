package org.team_alilm.product.image.repository

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.team_alilm.product.image.entity.ProductImageRow
import org.team_alilm.product.image.entity.ProductImageTable
import org.team_alilm.product.image.repository.projection.ProductImageProjection

@Repository
class ProductImageExposedRepository {

    fun fetchProductImageById(productId: Long): List<ProductImageRow> {
        return ProductImageTable
            .selectAll()
            .where {
                (ProductImageTable.productId eq productId) and
                        (ProductImageTable.isDelete eq false)
            }
            .map(ProductImageRow::from)
    }

    fun fetchProductImagesByProductIds(productIds: List<Long>): List<ProductImageProjection> {
        val table = ProductImageTable

        return table
            .select(table.id, table.productId, table.imageUrl)
            .where { (table.productId inList productIds) and (table.isDelete eq false) }
            .map {
                ProductImageProjection(
                    id = it[table.id].value,
                    productId = it[table.productId],
                    imageUrl = it[table.imageUrl]
                )
            }
    }
}