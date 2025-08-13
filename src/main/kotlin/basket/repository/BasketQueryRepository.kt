package org.team_alilm.basket.repository

import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import org.team_alilm.basket.controller.dto.response.MyBasketItem
import org.team_alilm.basket.controller.dto.response.MyBasketProductListResponse
import org.team_alilm.basket.entity.Basket
import org.team_alilm.basket.exposed.BasketTable
import org.team_alilm.product.entity.ProductTable

@Repository
class BasketQueryRepository {

    fun getMyBasketProductList(myBasketList: List<Basket>): MyBasketProductListResponse = transaction {
        if (myBasketList.isEmpty()) {
            return@transaction MyBasketProductListResponse(emptyList())
        }

        // 1) 필요한 productId만 추출
        val productIds = myBasketList.map { it.productId }.distinct()

        // 2) productId 기반으로 ProductTable join 조회
        val productRows = ProductTable
            .selectAll()
            .where { ProductTable.id inList productIds }
            .associateBy { it[ProductTable.id] } // productId → ResultRow 매핑

        // 3) productId별 대기 인원 수 집계
        val countCol = BasketTable.id.count().alias("waiting_count")
        val waitingMap = BasketTable
            .select(BasketTable.productId, countCol)
            .where { (BasketTable.productId inList productIds) and (BasketTable.isNotification eq false) }
            .groupBy(BasketTable.productId)
            .associate { it[BasketTable.productId] to (it[countCol] ?: 0L) }

        // 4) 매핑
        val items = myBasketList.mapNotNull { basket ->
            val productRow = productRows[basket.productId] ?: return@mapNotNull null
            MyBasketItem(
                basketId = basket.id!!,
                number = productRow[ProductTable.storeNumber],
                name = productRow[ProductTable.name],
                brand = productRow[ProductTable.brand],
                imageUrl = productRow[ProductTable.thumbnailUrl],
                store = productRow[ProductTable.store],
                price = productRow[ProductTable.price].toInt(),
                notification = basket.isNotification,
                notificationDate = basket.notificationDate,
                firstCategory = productRow[ProductTable.firstCategory],
                firstOption = productRow[ProductTable.firstOption],
                secondOption = productRow[ProductTable.secondOption],
                thirdOption = productRow[ProductTable.thirdOption],
                hidden = basket.isHidden,
                waitingCount = waitingMap[basket.productId] ?: 0L,
                productId = basket.productId
            )
        }

        MyBasketProductListResponse(items)
    }

    fun fetchWaitingCounts(productIds: List<Long>): Map<Long, Long> = transaction {
        if (productIds.isEmpty()) return@transaction emptyMap()

        val waitingCountExpr = BasketTable.id.count().alias("waiting_count")

        BasketTable
            .select(
                BasketTable.productId,
                waitingCountExpr
            )
            .where {
                (BasketTable.productId inList productIds) and
                        (BasketTable.isNotification eq false) and
                        (BasketTable.isHidden eq false) and
                        (BasketTable.isDelete eq false)
            }
            .groupBy(BasketTable.productId)
            .associate { row ->
                row[BasketTable.productId] to (row[waitingCountExpr] ?: 0L)
            }
    }
}