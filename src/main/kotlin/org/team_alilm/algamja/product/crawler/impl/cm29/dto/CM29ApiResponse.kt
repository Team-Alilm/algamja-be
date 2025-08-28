package org.team_alilm.algamja.product.crawler.impl.cm29.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class CM29ApiResponse(
    val result: String,
    val data: CM29ProductData?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CM29ProductData(
    val itemName: String,
    val itemNo: Long,
    val sellPrice: Long,
    val consumerPrice: Long?,
    val discountRate: Int?,
    val frontBrand: FrontBrand,
    val itemImages: List<ItemImage>,
    val frontCategoryInfo: List<CategoryInfo>,
    val optionItems: OptionItems?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FrontBrand(
    val brandNameKor: String?,
    val brandNameEng: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ItemImage(
    val imageUrl: String,
    val imageType: Int,
    val orderingIdx: Int
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CategoryInfo(
    val category1Name: String?,
    val category2Name: String?,
    val category3Name: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OptionItems(
    val list: List<OptionItem>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OptionItem(
    val optionName: String?,
    val optionValueList: List<OptionValue>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OptionValue(
    val optionValue: String?
)