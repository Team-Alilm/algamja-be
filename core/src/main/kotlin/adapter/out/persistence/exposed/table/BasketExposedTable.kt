package org.team_alilm.adapter.out.persistence.exposed.table

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

object BasketExposedTable : LongIdTable(name = "basket") {

    val createdDate: Column<Long> = long("created_date")
    val lastModifiedDate: Column<Long> = long("last_modified_date")
    val alilmDate: Column<Long?> = long("alilm_date").nullable()

    val isDeleted: Column<Boolean> = bool("is_delete").default(false)
    val isAlilm: Column<Boolean> = bool("is_alilm").default(false)
    val isHidden: Column<Boolean> = bool("is_hidden").default(false)

    val memberId: Column<Long> = long("member_id")
    val productId: Column<Long> = long("product_id")

    init {
        uniqueIndex("basket_member_product", memberId, productId)
    }
}