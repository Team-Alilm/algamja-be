package org.team_alilm.algamja.product.crawler.impl.musinsa.dto.option

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ColorInfo(
    val colorCode: String,          // 예: "2"
    val colorType: String           // 예: "MUSINSA"
)
