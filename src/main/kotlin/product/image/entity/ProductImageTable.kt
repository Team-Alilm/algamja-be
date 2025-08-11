package org.team_alilm.product.image.entity

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object ProductImageTable : Table("product_image") {
    // BaseEntity 공통 컬럼
    val id = long("id").autoIncrement()
    val createdDate = long("created_date")            // millis
    val lastModifiedDate = long("last_modified_date") // millis
    val isDelete = bool("is_delete").default(false)

    // 본문 컬럼
    val imageUrl = varchar("image_url", 512).uniqueIndex("ux_product_image_url")
    val productId = long("product_id")

    override val primaryKey = PrimaryKey(id, name = "pk_product_image")

    init {
        // 조회/정렬 최적화 인덱스
        index("idx_product_image_product_id", false, productId)
        index("idx_product_image_product_id_id", false, productId, id)
    }

    /** 소프트 삭제 제외 공통 조건 */
    fun notDeleted(): Op<Boolean> = (isDelete eq false)
}