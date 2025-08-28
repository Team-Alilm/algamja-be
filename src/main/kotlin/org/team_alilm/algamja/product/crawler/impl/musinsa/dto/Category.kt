package org.team_alilm.algamja.product.crawler.impl.musinsa.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Category(
    val categoryDepth1Code: String,
    val categoryDepth1Title: String,
    val categoryDepth1Name: String,
    val categoryDepth2Code: String,
    val categoryDepth2Title: String,
    val categoryDepth2Name: String,
    val categoryDepth3Code: String?,
    val categoryDepth3Title: String?,
    val categoryDepth3Name: String?,
    val categoryDepth4Code: String?,
    val categoryDepth4Title: String?,
    val categoryDepth4Name: String?,
    val storeCode: String?
)
