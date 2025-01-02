package org.team_alilm.application.port.`in`.use_case.product.crawling

import domain.product.Store

interface ProductCrawlingUseCase {

    fun crawling(command: ProductCrawlingCommand): CrawlingResult

    data class ProductCrawlingCommand(
        val url: String,
        val store: Store,
        val productNumber: Long
    )

    data class CrawlingResult(
        val number: Long,
        val name: String,
        val brand: String,
        val thumbnailUrl: String,
        val imageUrlList: List<String>,
        val firstCategory: String,
        val secondCategory: String?,
        val price: Int,
        val store: Store,
        val firstOptions: List<String>,
        val secondOptions: List<String>,
        val thirdOptions: List<String>
    )
}



