package org.team_alilm.application.port.`in`.use_case

import org.teamalilm.alilm.adapter.`in`.web.controller.products.CrawlingController.CrawlingRequest
import org.teamalilm.alilm.domain.Product

interface CrawlingUseCase {

    fun productCrawling(command: org.team_alilm.application.port.`in`.use_case.CrawlingUseCase.ProductCrawlingCommand): org.team_alilm.application.port.`in`.use_case.CrawlingUseCase.CrawlingResult

    data class ProductCrawlingCommand(
        val url: String
    ) {

        companion object {
            fun from(request: CrawlingRequest): org.team_alilm.application.port.`in`.use_case.CrawlingUseCase.ProductCrawlingCommand {
                return org.team_alilm.application.port.`in`.use_case.CrawlingUseCase.ProductCrawlingCommand(url = request.url)
            }
        }
    }

    data class CrawlingResult(
        val number: Long,
        val name: String,
        val brand: String,
        val imageUrl: String,
        val category: String,
        val price: Int,
        val store: Product.Store,
        val firstOptions: List<String>,
        val secondOptions: List<String>,
        val thirdOptions: List<String>
    )
}



