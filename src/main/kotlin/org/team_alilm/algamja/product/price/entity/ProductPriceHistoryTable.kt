package org.team_alilm.algamja.product.price.entity

import org.team_alilm.algamja.common.entity.BaseLongIdTable
import java.math.BigDecimal

object ProductPriceHistoryTable : BaseLongIdTable("product_price_history") {
    val productId = long("product_id")
    val price = decimal("price", 15, 0) // 원 단위 정수 가격
    val recordedAt = long("recorded_at") // 기록 시점 (timestamp)
    
    init {
        // 상품별 가격 히스토리 조회를 위한 인덱스
        index("idx_product_price_history_product_id", false, productId)
        
        // 시간순 정렬을 위한 복합 인덱스 (상품별 + 시간순)
        index("idx_product_price_history_product_time", false, productId, recordedAt)
        
        // 가격 0 이상 체크 제약
        check("chk_price_non_negative") { price greaterEq BigDecimal.ZERO }
    }
}