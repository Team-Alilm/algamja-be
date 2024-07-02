package org.teamalilm.alilmbe.service

import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.teamalilm.alilmbe.domain.product.entity.Product

@Service
class ProductCrawlingService {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun crawling(command: ProductCrawlingCommand) : ProductCrawlingResult {
        val doc = Jsoup
            .connect(command.url)
            .get()

        log.info(doc.tag().toString())

        val name = doc.select("h1.product_title").text()

        return ProductCrawlingResult(
            name = name,
            imageUrl = "",
            price = 0,
            store = Product.ProductInfo.Store.MUSINSA,
            option1 = "",
            option2 = "",
            option3 = ""
        )
    }

    data class ProductCrawlingCommand (
        val url: String
    )

    data class ProductCrawlingResult (
        val name: String,
        val imageUrl: String,
        val price: Int,
        val store: Product.ProductInfo.Store,
        val option1: String,
        val option2: String,
        val option3: String
    )
}