package org.team_alilm.product.entity

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table

object ProductTable : Table("product") {
    // BaseEntity 공통 컬럼
    val id = long("id").autoIncrement()
    val createdDate = long("created_date")            // millis
    val lastModifiedDate = long("last_modified_date") // millis
    val isDelete = bool("is_delete").default(false)

    // Product 전용 컬럼
    val storeNumber = long("store_number")
    val name = varchar("name", 200)
    val brand = varchar("brand", 120)
    val thumbnailUrl = varchar("thumbnail_url", 512)
    val store = varchar("store", 20)
    val firstCategory = varchar("first_category", 80)
    val secondCategory = varchar("second_category", 80).nullable()
    val price = decimal("price", 15, 0) // BigDecimal scale=0
    val firstOption = varchar("first_option", 120).nullable()
    val secondOption = varchar("second_option", 120).nullable()
    val thirdOption = varchar("third_option", 120).nullable()

    override val primaryKey = PrimaryKey(id)

    // 공통 조건 헬퍼
    fun notDeleted() = isDelete eq false
}