package org.team_alilm.quartz.job

import io.awspring.cloud.sqs.operations.SqsTemplate
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.out.*
import domain.product.Product

@Component
@Transactional(readOnly = true)
class SoldoutCheckJob(
    private val loadCrawlingProductsPort: LoadCrawlingProductsPort,
    private val sqsTemplate: SqsTemplate
) : Job {

    private val log = LoggerFactory.getLogger(SoldoutCheckJob::class.java)

    @Transactional
    override fun execute(context: JobExecutionContext) {
        val productList: List<Product> = loadCrawlingProductsPort.loadCrawlingProducts()

        productList.forEach {
            sqsTemplate.send("product-soldout-check-queue", it)
        }

        // 비동기 작업으로 전환해요.
//        coroutineScope.launch {
//            productList.chunked(10).forEach { chunk ->
//                launch {
//                    chunk.forEach { product ->
//                        try {
//                            val soldoutProduct = platformHandlerResolver
//                                .resolve(product.store)
//                                .process(product)
//
//                            if (soldoutProduct != null) {
//                                // 상품이 품절이 아닌 경우, 저장해요.
//                                sqsTemplate.send("AlilmQueue", product)
//                            }
//                        } catch (e: Exception) {
//                            log.info("Error processing product ${product.id}: ${e.message}")
//                            slackGateway.sendMessage("Error processing productId: ${product.id}: ${e.message}")
//                        }
//                    }
//                }
//            }
//        }
    }
}
