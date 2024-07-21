package org.teamalilm.alilmbe.application.port.`in`.use_case

import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.Store

typealias ProductCrawlingUseCase = (ProductCrawlingCommand) -> ProductCrawlingResult

data class ProductCrawlingCommand(
    val url: String
)

data class ProductCrawlingResult(
    val id: Long,
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