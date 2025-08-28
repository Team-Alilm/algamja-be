package org.team_alilm.algamja.product.crawler.impl.zigzag.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ZigzagApiResponse(
    val data: ResponseData
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ResponseData(
    @JsonProperty("pdp_option_info")
    val pdpOptionInfo: PdpOptionInfo
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PdpOptionInfo(
    @JsonProperty("catalog_product")
    val catalogProduct: CatalogProduct
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CatalogProduct(
    val id: String,
    val name: String,
    @JsonProperty("shop_name")
    val shopName: String,
    @JsonProperty("product_image_list")
    val productImageList: List<ProductImage>,
    @JsonProperty("product_price")
    val productPrice: ProductPrice,
    @JsonProperty("managed_category_list")
    val managedCategoryList: List<ManagedCategory>,
    @JsonProperty("product_option_list")
    val productOptionList: List<ProductOption>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductImage(
    val url: String,
    @JsonProperty("image_type")
    val imageType: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductPrice(
    @JsonProperty("max_price_info")
    val maxPriceInfo: MaxPriceInfo,
    @JsonProperty("final_discount_info")
    val finalDiscountInfo: FinalDiscountInfo?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MaxPriceInfo(
    val price: Long
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FinalDiscountInfo(
    @JsonProperty("discount_price")
    val discountPrice: Long
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ManagedCategory(
    val value: String,
    val depth: Int
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductOption(
    val order: Int,
    val name: String,
    @JsonProperty("value_list")
    val valueList: List<OptionValue>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OptionValue(
    val value: String
)