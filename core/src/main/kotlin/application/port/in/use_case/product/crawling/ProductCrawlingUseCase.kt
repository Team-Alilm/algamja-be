package org.team_alilm.application.port.`in`.use_case.product.crawling

import org.team_alilm.domain.product.Store

interface ProductCrawlingUseCase {

    fun crawling(command: ProductCrawlingCommand): CrawlingResult

    data class ProductCrawlingCommand(
        val url: String
    )

    data class CrawlingResult(
        val number: Long,
        val name: String,
        val brand: String,
        val thumbnailUrl: String,
        val firstCategory: String,
        val secondCategory: String?,
        val price: Int,
        val store: Store,
        val firstOptionName: String,
        val secondOptionName: String?,
        val thirdOptionName: String?,
        val firstOptions: List<String>,
        val secondOptions: List<String>,
        val thirdOptions: List<String>
    )
}



