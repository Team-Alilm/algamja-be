package org.team_alilm.product.crawler.impl.musinsa.dto.option

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class OptionItemColor(
    val optionItemNo: Long,
    val colorCode: String,
    val colorType: String
)