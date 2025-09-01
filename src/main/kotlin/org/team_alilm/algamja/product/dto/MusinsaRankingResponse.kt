package org.team_alilm.algamja.product.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class MusinsaRankingResponse(
    @JsonProperty("data")
    val data: MusinsaRankingData? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MusinsaRankingData(
    @JsonProperty("list")
    val list: List<MusinsaRankingItem> = emptyList(),
    @JsonProperty("modules")
    val modules: List<MusinsaModule> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MusinsaModule(
    @JsonProperty("type")
    val type: String? = null,
    @JsonProperty("items")
    val items: List<MusinsaProductItem> = emptyList(),
    @JsonProperty("multiColumn")
    val multiColumn: MusinsaMultiColumn? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MusinsaMultiColumn(
    @JsonProperty("list")
    val list: List<MusinsaProductItem> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MusinsaProductItem(
    @JsonProperty("id")
    val id: String? = null,
    @JsonProperty("type")
    val type: String? = null,
    @JsonProperty("info")
    val info: MusinsaProductInfo? = null,
    @JsonProperty("image")
    val image: MusinsaProductImage? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MusinsaProductInfo(
    @JsonProperty("brandName")
    val brandName: String? = null,
    @JsonProperty("productName")
    val productName: String? = null,
    @JsonProperty("finalPrice")
    val finalPrice: Int? = null,
    @JsonProperty("discountRatio")
    val discountRatio: Int? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MusinsaProductImage(
    @JsonProperty("url")
    val url: String? = null,
    @JsonProperty("rank")
    val rank: Int? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MusinsaRankingItem(
    @JsonProperty("item")
    val item: MusinsaItem? = null,
    @JsonProperty("ranking")
    val ranking: Int? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MusinsaItem(
    @JsonProperty("item_no")
    val itemNo: String? = null,
    @JsonProperty("item_id")
    val itemId: String? = null,
    @JsonProperty("item_name")
    val itemName: String? = null,
    @JsonProperty("brand_no")
    val brandNo: String? = null,
    @JsonProperty("brand_name")
    val brandName: String? = null,
    @JsonProperty("category")
    val category: String? = null,
    @JsonProperty("price")
    val price: Int? = null,
    @JsonProperty("sale_price")
    val salePrice: Int? = null,
    @JsonProperty("image_url")
    val imageUrl: String? = null,
    @JsonProperty("image_list")
    val imageList: List<String>? = null,
    @JsonProperty("url")
    val url: String? = null,
    @JsonProperty("sale_yn")
    val saleYn: String? = null,
    @JsonProperty("discount_rate")
    val discountRate: Int? = null
)