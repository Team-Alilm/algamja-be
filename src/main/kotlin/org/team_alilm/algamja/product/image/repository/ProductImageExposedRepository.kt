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

    /** 단일 이미지 저장 */
    fun save(productId: Long, imageUrl: String, imageOrder: Int = 0): ProductImageRow {
        ProductImageTable.insertAudited { row ->
            row[ProductImageTable.productId] = productId
            row[ProductImageTable.imageUrl] = imageUrl
            // imageOrder 컬럼이 있다면 사용
        }
        
        // 방금 삽입한 이미지를 조회 (productId와 imageUrl로 조회)
        return ProductImageTable
            .selectAll()
            .where { 
                (ProductImageTable.productId eq productId) and 
                (ProductImageTable.imageUrl eq imageUrl) and
                (ProductImageTable.isDelete eq false)
            }
            .orderBy(ProductImageTable.id to org.jetbrains.exposed.sql.SortOrder.DESC)
            .limit(1)
            .single()
            .let(ProductImageRow::from)
    }
    
    /** 이미지 존재 여부 확인 */
    fun existsByProductIdAndImageUrl(productId: Long, imageUrl: String): Boolean {
        return ProductImageTable
            .selectAll()
            .where { 
                (ProductImageTable.productId eq productId) and 
                (ProductImageTable.imageUrl eq imageUrl) and
                (ProductImageTable.isDelete eq false)
            }
            .count() > 0
    }
    
    /** 단일 이미지 저장 (중복 체크 포함) */
    fun saveIfNotExists(productId: Long, imageUrl: String, imageOrder: Int = 0): ProductImageRow? {
        return try {
            // 동일 상품에 같은 이미지가 이미 존재하는지 확인
            val existing = ProductImageTable
                .selectAll()
                .where { 
                    (ProductImageTable.productId eq productId) and
                    (ProductImageTable.imageUrl eq imageUrl) and
                    (ProductImageTable.isDelete eq false)
                }
                .firstOrNull()
            
            if (existing != null) {
                // 이미 이 상품에 등록된 이미지
                return null
            }
            
            // 존재하지 않으면 저장
            save(productId, imageUrl, imageOrder)
        } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
            // Duplicate entry 에러는 무시 (동시성 문제로 다른 스레드에서 이미 삽입한 경우)
            if (e.cause?.message?.contains("Duplicate entry") == true) {
                null
            } else {
                throw e
            }
        } catch (e: Exception) {
            // 기타 예외는 재발생
            throw e
        }
    }
}