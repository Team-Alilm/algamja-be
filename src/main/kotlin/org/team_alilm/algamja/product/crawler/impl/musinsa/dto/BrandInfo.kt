package org.team_alilm.algamja.product.crawler.impl.musinsa.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class BrandInfo(
    val brandName: String
)
