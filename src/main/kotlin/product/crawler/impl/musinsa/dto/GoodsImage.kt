package org.team_alilm.product.crawler.impl.musinsa.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GoodsImage(
    val kind: String,
    val repYn: Boolean,   // true/false 내려오므로 Boolean
    val bigYn: Boolean,   // true/false 내려오므로 Boolean
    val width: Int,
    val height: Int,
    val seq: Int,
    val imageUrl: String
)
