package org.team_alilm.application.port.`in`.use_case

import org.team_alilm.domain.product.Store

interface ProductCrawlingUseCase {

    fun crawling(command: ProductCrawlingCommand): CrawlingResult

    data class ProductCrawlingCommand(
        val url: String
    )

    data class CrawlingResult(
        val id: Long,
        val number: Long,
        val name: String,
        val brand: String,
        val thumbnailUrl: String,
        val category: String,
        val price: Int,
        val store: Store,
        val firstOption: String,
        val secondOption: String?,
        val thirdOption: String?
    )
}



