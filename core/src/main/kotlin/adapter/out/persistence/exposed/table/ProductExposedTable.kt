package org.team_alilm.adapter.out.persistence.exposed.table

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

object ProductExposedTable : LongIdTable("product") {

    val createdDate: Column<Long> = long("created_date")
    val lastModifiedDate: Column<Long> = long("last_modified_date")
    val isDeleted: Column<Boolean> = bool("is_delete")

    val brand: Column<String> = varchar("brand", 255)
    val name: Column<String> = varchar("name", 255)
    val number: Column<Long> = long("number")
    val price: Column<Int> = integer("price")

    val firstCategory: Column<String> = varchar("first_category", 255)
    val secondCategory: Column<String?> = varchar("second_category", 255).nullable()

    val firstOption: Column<String> = varchar("first_option", 255)
    val secondOption: Column<String?> = varchar("second_option", 255).nullable()
    val thirdOption: Column<String?> = varchar("third_option", 255).nullable()

    val store: Column<String> = varchar("store", 20)

    val thumbnailUrl: Column<String> = varchar("thumbnail_url", 255)

    init {
        uniqueIndex(
            "tag_key_number_size_color",
            store, number, firstOption, secondOption, thirdOption
        )
    }

}