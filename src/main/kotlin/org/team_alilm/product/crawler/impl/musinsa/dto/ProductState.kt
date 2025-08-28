package org.team_alilm.product.crawler.impl.musinsa.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductState(
    val goodsNo: Long,
    val goodsNm: String,
    val thumbnailImageUrl: String,
    val brand: String,
    val brandInfo: BrandInfo,
    val category: Category,
    val goodsImages: List<GoodsImage>,
    val goodsPrice: GoodsPrice,
)
