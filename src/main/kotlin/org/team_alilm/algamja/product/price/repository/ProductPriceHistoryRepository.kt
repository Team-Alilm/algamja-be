package org.team_alilm.algamja.product.price.repository

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import org.team_alilm.algamja.common.entity.insertAudited
import org.team_alilm.algamja.product.price.entity.ProductPriceHistoryRow
import org.team_alilm.algamja.product.price.entity.ProductPriceHistoryTable
import java.math.BigDecimal

@Repository
class ProductPriceHistoryRepository {

    /**
     * 상품의 가격 히스토리를 기록합니다.
     */
    fun recordPriceHistory(productId: Long, price: BigDecimal, recordedAt: Long = System.currentTimeMillis()): ProductPriceHistoryRow {
        val insertedId = ProductPriceHistoryTable.insertAudited {
            it[ProductPriceHistoryTable.productId] = productId
            it[ProductPriceHistoryTable.price] = price
            it[ProductPriceHistoryTable.recordedAt] = recordedAt
        }
        
        return ProductPriceHistoryTable
            .selectAll()
            .where { ProductPriceHistoryTable.id eq insertedId[ProductPriceHistoryTable.id] }
            .map(ProductPriceHistoryRow::from)
            .first()
    }

    /**
     * 특정 상품의 가격 히스토리를 시간순(최신순)으로 조회합니다.
     */
    fun fetchPriceHistoryByProductId(productId: Long, limit: Int? = null): List<ProductPriceHistoryRow> {
        val query = ProductPriceHistoryTable
            .selectAll()
            .where {
                (ProductPriceHistoryTable.productId eq productId) and
                (ProductPriceHistoryTable.isDelete eq false)
            }
            .orderBy(ProductPriceHistoryTable.recordedAt to SortOrder.DESC)
        
        return if (limit != null) {
            query.limit(limit).map(ProductPriceHistoryRow::from)
        } else {
            query.map(ProductPriceHistoryRow::from)
        }
    }

    /**
     * 특정 기간의 가격 히스토리를 조회합니다.
     */
    fun fetchPriceHistoryByProductIdAndPeriod(
        productId: Long,
        startTime: Long,
        endTime: Long
    ): List<ProductPriceHistoryRow> {
        return ProductPriceHistoryTable
            .selectAll()
            .where {
                (ProductPriceHistoryTable.productId eq productId) and
                (ProductPriceHistoryTable.isDelete eq false) and
                (ProductPriceHistoryTable.recordedAt greaterEq startTime) and
                (ProductPriceHistoryTable.recordedAt lessEq endTime)
            }
            .orderBy(ProductPriceHistoryTable.recordedAt to SortOrder.ASC)
            .map(ProductPriceHistoryRow::from)
    }

    /**
     * 특정 상품의 최신 가격 기록을 조회합니다.
     */
    fun fetchLatestPriceHistory(productId: Long): ProductPriceHistoryRow? {
        return ProductPriceHistoryTable
            .selectAll()
            .where {
                (ProductPriceHistoryTable.productId eq productId) and
                (ProductPriceHistoryTable.isDelete eq false)
            }
            .orderBy(ProductPriceHistoryTable.recordedAt to SortOrder.DESC)
            .limit(1)
            .map(ProductPriceHistoryRow::from)
            .firstOrNull()
    }

    /**
     * 가격이 변경된 상품들의 최신 가격과 이전 가격을 비교합니다.
     */
    fun fetchPriceChangedProducts(limit: Int = 100): List<ProductPriceHistoryRow> {
        return ProductPriceHistoryTable
            .selectAll()
            .where { ProductPriceHistoryTable.isDelete eq false }
            .orderBy(ProductPriceHistoryTable.recordedAt to SortOrder.DESC)
            .limit(limit)
            .map(ProductPriceHistoryRow::from)
    }

    /**
     * 특정 상품의 가격 변동이 있었는지 확인합니다.
     */
    fun hasPriceChanged(productId: Long, currentPrice: BigDecimal): Boolean {
        val latestHistory = fetchLatestPriceHistory(productId)
        return latestHistory == null || latestHistory.price != currentPrice
    }
    
    /**
     * 오래된 가격 히스토리를 삭제합니다.
     */
    fun deleteOldPriceHistory(beforeTimestamp: Long): Int {
        return ProductPriceHistoryTable
            .update({ 
                (ProductPriceHistoryTable.recordedAt less beforeTimestamp) and
                (ProductPriceHistoryTable.isDelete eq false)
            }) {
                it[isDelete] = true
            }
    }
}