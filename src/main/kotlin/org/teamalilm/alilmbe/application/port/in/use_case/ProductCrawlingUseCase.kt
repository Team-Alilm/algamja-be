package org.teamalilm.alilmbe.application.port.`in`.use_case

import org.teamalilm.alilmbe.adapter.`in`.web.controller.ProductCrawlingController.ProductScrapingRequest
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.Store

typealias ProductCrawlingUseCase = (ProductCrawlingCommand) -> ProductCrawlingResult

data class ProductCrawlingCommand(
    val url: String
) {

    companion object {
        fun from(request: ProductScrapingRequest): ProductCrawlingCommand {
            return ProductCrawlingCommand(url = request.url)
        }
    }
}

data class ProductCrawlingResult(
    val number: Long,
    val name: String,
    val brand: String,
    val imageUrl: String,
    val category: String,
    val price: Int,
    val store: Store,
    val option1List: List<String>,
    val option2List: List<String>,
    val option3List: List<String>
)