package org.team_alilm.basket.entity

import org.jetbrains.exposed.sql.Table

object BasketTable : Table("basket") {
    val id = long("id").autoIncrement()
    val memberId = long("member_id")
    val productId = long("product_id")
    val isNotification = bool("is_notification").default(false)
    val notificationDate = long("notification_date").nullable()
    val isHidden = bool("is_hidden").default(false)

    override val primaryKey = PrimaryKey(id, name = "pk_basket")

    init {
        index(false, memberId) // idx_basket_member_id
        index(false, productId) // idx_basket_product_id
        index(false, memberId, productId) // idx_basket_member_product
    }
}