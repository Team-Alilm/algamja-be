package org.team_alilm.algamja.product.crawler.impl.ably.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class AblyApiResponse(
    @JsonProperty("goods")
    val goods: AblyGoods?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AblyGoods(
    @JsonProperty("sno")
    val sno: Long,
    
    @JsonProperty("name")
    val name: String,
    
    @JsonProperty("market")
    val market: AblyMarket?,
    
    @JsonProperty("price_info")
    val priceInfo: AblyPriceInfo?,
    
    @JsonProperty("cover_images")
    val coverImages: List<String>?,
    
    @JsonProperty("display_categories")
    val displayCategories: List<AblyCategory>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AblyMarket(
    @JsonProperty("sno")
    val sno: Long,
    
    @JsonProperty("name")
    val name: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AblyPriceInfo(
    @JsonProperty("consumer")
    val consumer: Long?,
    
    @JsonProperty("thumbnail_price")
    val thumbnailPrice: Long
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AblyCategory(
    @JsonProperty("sno")
    val sno: Long,
    
    @JsonProperty("name")
    val name: String,
    
    @JsonProperty("depth")
    val depth: Int
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AblyOptionsResponse(
    @JsonProperty("name")
    val name: String?,
    
    @JsonProperty("option_components")
    val optionComponents: List<AblyOptionComponent>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AblyOptionComponent(
    @JsonProperty("sno")
    val sno: Long,
    
    @JsonProperty("depth")
    val depth: Int,
    
    @JsonProperty("name")
    val name: String,
    
    @JsonProperty("is_final_depth")
    val isFinalDepth: Boolean,
    
    @JsonProperty("goods_option_sno")
    val goodsOptionSno: Long
)