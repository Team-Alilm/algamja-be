package org.team_alilm.product.image.entity

import org.team_alilm.common.entity.BaseLongIdTable

object ProductImageTable : BaseLongIdTable("product_image") {
    // ===== 본문 컬럼 =====
    val imageUrl = varchar("image_url", 512).uniqueIndex("ux_product_image_url")
    val productId = long("product_id")

    init {
        // 조회/정렬 최적화 인덱스
        index("idx_product_image_product_id", isUnique = false, columns = arrayOf(productId))
        index("idx_product_image_product_id_id", isUnique = false, columns = arrayOf(productId, id))
    }
}