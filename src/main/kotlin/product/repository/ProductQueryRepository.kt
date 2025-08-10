package org.team_alilm.product.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import org.team_alilm.product.controller.dto.ProductDetailResponse
import org.team_alilm.product.entity.ProductTable
import org.team_alilm.product.image.entity.ProductImageTable

@Repository
class ProductQueryRepository {

    fun findProductDetail(productId: Long): ProductDetailResponse? = transaction {
        val query = ProductTable
            .join(ProductImageTable, JoinType.LEFT, ProductTable.id, ProductImageTable.productId)
            .select( // slice 대체
                ProductTable.id,
                ProductTable.storeNumber,
                ProductTable.name,
                ProductTable.brand,
                ProductTable.thumbnailUrl,
                ProductTable.store,
                ProductTable.price,
                ProductTable.firstOption,
                ProductTable.secondOption,
                ProductTable.thirdOption,
                ProductImageTable.imageUrl
            )
            .where { ProductTable.id eq productId }
            .orderBy(ProductImageTable.id to SortOrder.ASC)

        var first: ResultRow? = null
        val images = mutableListOf<String>()

        query.forEach { row ->
            if (first == null) first = row
            row[ProductImageTable.imageUrl].let(images::add) // null 안전
        }

        val r0 = first ?: return@transaction null

        ProductDetailResponse(
            id = r0[ProductTable.id],
            number = r0[ProductTable.storeNumber],
            name = r0[ProductTable.name],
            brand = r0[ProductTable.brand],
            thumbnailUrl = r0[ProductTable.thumbnailUrl],
            imageUrlList = images.distinct(),
            store = r0[ProductTable.store],
            price = r0[ProductTable.price].toLong(),
            firstOption = r0[ProductTable.firstOption],
            secondOption = r0[ProductTable.secondOption],
            thirdOption = r0[ProductTable.thirdOption],
            waitingCount = 0L
        )
    }
}