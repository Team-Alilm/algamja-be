package org.team_alilm.algamja.product.crawler.impl.musinsa.dto.option

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class OptionItem(
    val no: Long,
    val goodsNo: Long,
    val optionValueNos: List<Long> = emptyList(),
    val managedCode: String?,
    val price: Int,
    val activated: Boolean,
    val outOfStock: Boolean,
    val isSoldOut: Boolean,
    val isRedirect: Boolean,
    val isDeleted: Boolean,
    val optionValues: List<OptionItemValue> = emptyList(),
    val colors: List<OptionItemColor> = emptyList(),
    val remainQuantity: Int,
    val isHeadOfficeDelivery: Boolean,
    val relatedOption: Any?
)