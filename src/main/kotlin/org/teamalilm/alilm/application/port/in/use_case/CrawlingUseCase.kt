package org.teamalilm.alilm.application.port.`in`.use_case

import org.teamalilm.alilm.adapter.`in`.web.controller.CrawlingController.CrawlingRequest
import org.teamalilm.alilm.domain.Product

interface CrawlingUseCase {

    fun productCrawling(command: ProductCrawlingCommand): CrawlingResult

    data class ProductCrawlingCommand(
        val url: String
    ) {

        companion object {
            fun from(request: CrawlingRequest): ProductCrawlingCommand {
                return ProductCrawlingCommand(url = request.url)
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
        val option1List: List<String>,
        val option2List: List<String>,
        val option3List: List<String>
    )
}



