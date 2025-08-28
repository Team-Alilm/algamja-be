package org.team_alilm.algamja.product.crawler.impl.musinsa.dto.option

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class OptionData(
    val basic: List<BasicOption> = emptyList(),
    val optionItems: List<OptionItem> = emptyList()
)
