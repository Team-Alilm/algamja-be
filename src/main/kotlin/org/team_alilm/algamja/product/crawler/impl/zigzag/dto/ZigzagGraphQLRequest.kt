package org.team_alilm.algamja.product.crawler.impl.zigzag.dto

data class ZigzagGraphQLRequest(
    val query: String,
    val variables: Map<String, Any>
)