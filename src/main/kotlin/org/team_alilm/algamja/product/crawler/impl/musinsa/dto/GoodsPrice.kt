package org.team_alilm.algamja.product.crawler.impl.musinsa.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GoodsPrice(
    val salePrice: Int,
    val normalPrice: Int,
    val discountRate: Int,
    val type: String,
    val isSale: Boolean,
    val savePoint: Int,
    val savePointPercent: Int,
    val partnerInformation: String?,   // null 가능 → nullable
    val memberDiscountRate: Int,
    val memberSavePointRate: Int,
    val memberSaveMoneyRate: Int,
    val partnerDiscountOn: Boolean
)