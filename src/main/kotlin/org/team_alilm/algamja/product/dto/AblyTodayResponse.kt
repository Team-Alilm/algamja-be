package org.team_alilm.algamja.product.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 에이블리 TODAY API 응답 DTO
 * https://api.a-bly.com/api/v2/screens/TODAY/
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class AblyTodayResponse(
    @JsonProperty("components")
    val components: List<AblyComponent>? = null,
    
    @JsonProperty("next_token")
    val nextToken: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AblyComponent(
    @JsonProperty("type")
    val type: AblyComponentType? = null,
    
    @JsonProperty("entity")
    val entity: AblyEntity? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AblyComponentType(
    @JsonProperty("item_list")
    val itemList: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AblyEntity(
    @JsonProperty("item_list")
    val itemList: List<AblyItemWrapper>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AblyItemWrapper(
    @JsonProperty("item")
    val item: AblyTodayGoods? = null,
    
    // nextToken 사용 시 다른 구조: {"type": "GOODS_CARD", "item_entity": {"item": {...}}}
    @JsonProperty("item_entity")
    val itemEntity: AblyItemEntity? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AblyItemEntity(
    @JsonProperty("item")
    val item: AblyTodayGoods? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AblyTodayGoods(
    @JsonProperty("sno")
    val sno: Long? = null,
    
    @JsonProperty("name")
    val name: String? = null,
    
    @JsonProperty("market_name")
    val marketName: String? = null,
    
    @JsonProperty("price")
    val price: Long? = null,
    
    @JsonProperty("image")
    val image: String? = null,
    
    @JsonProperty("category_name")
    val categoryName: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AblyTodayMarket(
    @JsonProperty("sno")
    val sno: Long? = null,
    
    @JsonProperty("name")
    val name: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AblyTodayPriceInfo(
    @JsonProperty("consumer")
    val consumer: Long? = null,
    
    @JsonProperty("thumbnail_price")
    val thumbnailPrice: Long? = null,
    
    @JsonProperty("sell")
    val sell: Long? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AblyTodayCategory(
    @JsonProperty("sno")
    val sno: Long? = null,
    
    @JsonProperty("name")
    val name: String? = null,
    
    @JsonProperty("depth")
    val depth: Int? = null
)