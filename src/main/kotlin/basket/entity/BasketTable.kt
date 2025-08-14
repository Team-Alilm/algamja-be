package org.team_alilm.basket.exposed

import org.jetbrains.exposed.sql.Table

/**
 * basket 테이블 (JPA Basket 엔티티 대응)
 * - BaseEntity 컬럼 포함: id, created_date, last_modified_date, is_delete
 */
object BasketTable : Table("basket") {
    // PK (IDENTITY/AUTO_INCREMENT)
    val id = long("id").autoIncrement()

    // BaseEntity 공통 컬럼
    val createdDate = long("created_date")            // millis
    val lastModifiedDate = long("last_modified_date") // millis
    val isDelete = bool("is_delete").default(false)

    // Basket 컬럼
    val memberId = long("member_id")
    val productId = long("product_id")
    val isNotification = bool("is_notification").default(false)
    val notificationDate = long("notification_date").nullable()
    val isHidden = bool("is_hidden").default(false)

    override val primaryKey = PrimaryKey(id, name = "pk_basket")

    init {
        // JPA @Index 와 동일하게 구성
//        index(name = "idx_basket_member_id", columns = arrayOf(memberId))
//        index(name = "idx_basket_product_id", columns = arrayOf(productId))
//        index(name = "idx_basket_member_product", columns = arrayOf(memberId, productId))
    }
}