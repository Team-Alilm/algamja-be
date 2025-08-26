package org.team_alilm.product.crawler.impl.musinsa.dto.option

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class BasicOption(
    val no: Long,
    val type: String,
    val displayType: String,
    val name: String,                 // 예: "색상", "사이즈" 등 (네 샘플은 "C")
    val standardOptionNo: Long?,
    val sequence: Int,
    val isDeleted: Boolean,
    val optionValues: List<OptionValue> = emptyList()
)