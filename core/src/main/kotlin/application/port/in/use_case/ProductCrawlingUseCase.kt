package org.team_alilm.application.port.`in`.use_case

import org.team_alilm.domain.product.Store

interface ProductCrawlingUseCase {

    fun productCrawling(command: ProductCrawlingCommand): CrawlingResult

    data class ProductCrawlingCommand(
        val url: String
    )

    data class CrawlingResult(
        val number: Long,
        val name: String,
        val brand: String,
        val imageUrl: String,
        val category: String,
        val price: Int,
        val store: Store,
        val firstOptions: List<String>,
        val secondOptions: List<String>,
        val thirdOptions: List<String>
    )
}



