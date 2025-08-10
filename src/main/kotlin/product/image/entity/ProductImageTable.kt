package org.team_alilm.product.image.entity

import org.jetbrains.exposed.sql.Table

object ProductImageTable : Table("product_image") {
    val id = long("id").autoIncrement()
    val imageUrl = varchar("image_url", 512).uniqueIndex()
    val productId = long("product_id") // FK 없이 저장
    override val primaryKey = PrimaryKey(id)
}