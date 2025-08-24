package org.team_alilm.product.crawler.impl.musinsa.dto.option

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class OptionValue(
    val no: Long,
    val optionNo: Long,
    val name: String,               // 예: "MJUA754 (블랙)", "XS"
    val code: String,
    val sequence: Int,
    val standardOptionValueNo: Long?,
    val color: ColorInfo?,          // 컬러칩이 존재하는 경우에만 값 있음
    val isDeleted: Boolean,
    val imageUrl: String?
)
