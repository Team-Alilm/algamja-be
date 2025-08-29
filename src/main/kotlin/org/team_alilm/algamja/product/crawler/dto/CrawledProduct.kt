package org.team_alilm.algamja.product.crawler.dto

import org.team_alilm.algamja.common.enums.Store
import java.math.BigDecimal

data class CrawledProduct(
    val storeNumber: Long,
    val name: String,
    val brand: String,
    val thumbnailUrl: String,
    val imageUrls: List<String>,
    val store: Store,          // 예: "MUSINSA"
    val price: BigDecimal,
    val firstCategory: String,
    val secondCategory: String?,
    val firstOptions: List<String>,
    val secondOptions: List<String>,
    val thirdOptions: List<String>
)