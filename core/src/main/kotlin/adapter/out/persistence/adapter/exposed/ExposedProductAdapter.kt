package org.team_alilm.adapter.out.persistence.adapter.exposed

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.exposed.table.BasketExposedTable
import org.team_alilm.adapter.out.persistence.exposed.table.ProductExposedTable
import org.team_alilm.adapter.out.persistence.repository.product.ProductAndWaitingCount
import org.team_alilm.application.port.out.LoadFilteredProductListPort

@Component
class ExposedProductAdapter : LoadFilteredProductListPort {

    override fun getFilteredProductList(
        category: String?,
        size: Int,
        page: Int,
        sort: String
    ): List<ProductAndWaitingCount> = transaction {

        val countAlias = BasketExposedTable.id.count().alias("waitingCount")

        val baseQuery = (ProductExposedTable leftJoin BasketExposedTable)
            .slice {
                (BasketExposedTable.isDeleted eq false) and
                (BasketExposedTable.isAlilm eq false) and
                (BasketExposedTable.isHidden eq false) and
                cursorCondition(countAlias, lastWaitingCount, lastProductId)
            }

    }

    // 커서 조건 정의
    private fun cursorCondition(
        countAlias: ExpressionWithColumnType<Long>,
        lastCount: Long?,
        lastId: Long?
    ): Op<Boolean> {
        return if (lastCount != null && lastId != null) {
            // waitingCount < last OR (waitingCount == last AND id < lastId)
            (countAlias less lastCount) or
                    ((countAlias eq lastCount) and (ProductExposedTable.id less lastId))
        } else {
            Op.TRUE // 첫 페이지
        }
    }
}