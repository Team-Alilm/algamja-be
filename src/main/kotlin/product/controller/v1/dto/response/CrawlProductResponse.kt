package org.team_alilm.product.controller.v1.dto.response

data class CrawlProductResponse(
    val name: String,
    val thumbnailUrl: String,
    val firstOptions: List<String>,
    val secondOptions: List<String>,
    val thirdOptions: List<String>,
)