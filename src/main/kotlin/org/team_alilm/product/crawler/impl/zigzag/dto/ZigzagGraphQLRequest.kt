package org.team_alilm.product.crawler.impl.zigzag.dto

data class ZigzagGraphQLRequest(
    val query: String,
    val variables: Map<String, Any>
)