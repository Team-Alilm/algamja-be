package org.team_alilm.product.entity

import org.team_alilm.common.entity.BaseLongIdTable
import common.enums.Store
import java.math.BigDecimal

object ProductTable : BaseLongIdTable("product") {
    // 컬럼 정의
    val storeNumber   = long("store_number")
    val name          = varchar("name", 200)
    val brand         = varchar("brand", 120)
    val thumbnailUrl  = varchar("thumbnail_url", 512)
    val store         = enumerationByName<Store>("store", 20)

    val firstCategory  = varchar("first_category", 80)
    val secondCategory = varchar("second_category", 80).nullable()

    // 원 단위 정수 가격 (precision=15, scale=0)
    val price = decimal("price", 15, 0)

    val firstOption  = varchar("first_option", 120).nullable()
    val secondOption = varchar("second_option", 120).nullable()
    val thirdOption  = varchar("third_option", 120).nullable()

    init {
        // ✅ Unique 제약
        uniqueIndex("uk_store_store_number", store, storeNumber)
        uniqueIndex(
            "uk_product_options",
            store, storeNumber, firstOption, secondOption, thirdOption
        )

        // ✅ 인덱스
        index("idx_brand", false, brand)
        index("idx_category1_2", false, firstCategory, secondCategory)
        index("idx_price", false, price)

        // ✅ CHECK 제약 (가격 0 이상)
        check("chk_price_non_negative") { price greaterEq BigDecimal.ZERO }
    }
}