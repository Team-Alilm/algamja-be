package org.team_alilm.algamja.product.image.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.team_alilm.algamja.common.entity.insertAudited
import org.team_alilm.algamja.product.image.entity.ProductImageRow
import org.team_alilm.algamja.product.image.entity.ProductImageTable
import org.team_alilm.algamja.product.image.repository.projection.ProductImageProjection

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

    /** 기존 이미지 전부 삭제 후 전달한 목록으로 재삽입 */
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