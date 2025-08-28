package org.team_alilm.algamja.product.crawler.impl.musinsa.dto.option

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class OptionApiResponse(
    val data: OptionData?,
)