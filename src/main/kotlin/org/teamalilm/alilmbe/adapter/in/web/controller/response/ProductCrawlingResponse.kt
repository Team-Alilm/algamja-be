package org.teamalilm.alilmbe.adapter.`in`.web.controller.response

import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.Store

data class ProductCrawlingResponse(
    val name: String,
    val brand: String,
    val imageUrl: String,
    val category: String,
    val price: Int,
    val store: Store,
    val option1: List<String>,
    val option2: List<String>,
    val option3: List<String>
)