package org.team_alilm.algamja.product.crawler.impl.musinsa.dto.option

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class OptionItemValue(
    val no: Long,
    val name: String,                 // 조합 내 값명
    val code: String,
    val optionNo: Long,
    val optionName: String,           // 조합 내 이 값이 속한 옵션명 (예: "C")
    val imageUrl: String?
)