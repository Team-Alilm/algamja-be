package org.teamalilm.alilmbe.domain.tracer

import java.net.URI
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.teamalilm.alilmbe.domain.basket.repository.BasketRepository
import org.teamalilm.alilmbe.domain.product.repository.ProductRepository

@Component
class SoldoutCheckJob(
    val productRepository: ProductRepository,
    val basketRepository: BasketRepository

) : Job {

    private val log = LoggerFactory.getLogger(SoldoutCheckJob::class.java)
    override fun execute(context: JobExecutionContext) {
        log.info("SoldoutCheckJob is running")

        val soldoutProductIds = mutableListOf<Long>()
        val restClient = RestClient.create()
        val products = productRepository.findAllByOrderByCreatedDateDesc()

        products.forEach {
            val apiUrl = SoldoutScheduler.API_URL_TEMPLATE.format(it.productInfo.number)
            val response = restClient.get()
                .uri(URI.create(apiUrl))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body<SoldoutScheduler.SoldoutCheckResponse>()

            log.error("무신사 api의 요청이 성공하지 못했어요. URL: $apiUrl, 상품번호: ${it.productInfo.number}")

            val option1 = it.productInfo.option1
            val option2 = it.productInfo.option2

            val isSoldOut = response?.data?.basic?.firstOrNull { it.name == option1 }?.run {
                if (subOptions.isNotEmpty()) {
                    subOptions.find { it.name == option2 }?.run {
                        isSoldOut
                    }
                        ?: throw IllegalStateException("상품 옵션2을 찾지 못했어요. 상품번호: ${it.productInfo.number} 옵션1: $option1")
                } else {
                    isSoldOut
                }
            }
                ?: throw IllegalStateException("상품 옵션1을 찾지 못했어요. 상품번호: ${it.productInfo.number} 옵션1: $option1")

            if (!isSoldOut) {
                log.info("상품이 품절되지 않았어요. 상품번호: ${it.productInfo.number}")
                soldoutProductIds.add(it.id!!)
            }
        }

        basketRepository.deleteByProductIds(soldoutProductIds)
    }
}