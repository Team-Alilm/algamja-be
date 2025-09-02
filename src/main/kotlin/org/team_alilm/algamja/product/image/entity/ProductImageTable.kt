package org.team_alilm.algamja.product.image.entity

import org.team_alilm.algamja.common.entity.BaseLongIdTable

object ProductImageTable : BaseLongIdTable("product_image") {
    // ===== 본문 컬럼 =====
    val imageUrl = varchar("image_url", 512)
    val productId = long("product_id")
    val imageOrder = integer("image_order").default(0)

    init {
        // 동일 상품에 같은 이미지 중복 방지 (productId + imageUrl 복합 unique)
        uniqueIndex("ux_product_image_product_url", productId, imageUrl)
        
        // 조회/정렬 최적화 인덱스
        index("idx_product_image_product_id", isUnique = false, columns = arrayOf(productId))
        index("idx_product_image_product_id_order", isUnique = false, columns = arrayOf(productId, imageOrder))
        
        // 이미지 URL 조회 최적화 인덱스
        index("idx_product_image_url", isUnique = false, columns = arrayOf(imageUrl))
    }
}