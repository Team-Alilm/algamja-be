package org.team_alilm.product.image.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.team_alilm.common.entity.insertAudited
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

    /**
     * Retrieves non-deleted image projections for the given product IDs.
     *
     * Returns a list of ProductImageProjection containing the image id, productId, and imageUrl
     * for every row in ProductImageTable whose productId is in `productIds` and isDelete is false.
     *
     * @param productIds List of product IDs to fetch images for.
     * @return List of ProductImageProjection matching the provided product IDs.
     */
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

    /**
     * Replace all images for a product by hard-deleting existing rows and inserting the provided URLs.
     *
     * Performs a hard delete of all ProductImageTable rows for the given productId, then inserts each
     * non-empty, trimmed, distinct URL from imageUrls as a new row. URLs are deduplicated and blank
     * entries are ignored. Inserts use the audited insert helper and may populate additional columns
     * (e.g., sortOrder) if needed.
     *
     * @param imageUrls List of image URLs to insert; entries will be trimmed, empty strings removed,
     * and duplicates discarded before insertion.
     */
    fun replaceImages(productId: Long, imageUrls: List<String>) {
        // 1) 기존 삭제 (하드 삭제)
        ProductImageTable.deleteWhere { ProductImageTable.productId eq productId }

        // 2) 새로 삽입 (중복/공백 제거 권장)
        imageUrls.asSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .forEach { url ->
                ProductImageTable.insertAudited { row ->
                    row[ProductImageTable.productId] = productId
                    row[ProductImageTable.imageUrl]  = url
                    // row[ProductImageTable.sortOrder] = ...  // 정렬 컬럼 있으면 사용
                }
            }
    }
}