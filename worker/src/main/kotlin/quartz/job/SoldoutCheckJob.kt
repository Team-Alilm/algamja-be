package org.team_alilm.quartz.job

import io.awspring.cloud.sqs.operations.SqsTemplate
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.out.*
import domain.product.Product
import org.team_alilm.application.port.out.gateway.SendSlackGateway

@Component
@Transactional(readOnly = true)
class SoldoutCheckJob(
    private val loadCrawlingProductsPort: LoadCrawlingProductsPort,
    private val sqsTemplate: SqsTemplate,
    private val sendSlackGateway: SendSlackGateway
) : Job {

    @Transactional
    override fun execute(context: JobExecutionContext) {
        val productList: List<Product> = loadCrawlingProductsPort.loadCrawlingProducts()

        productList.forEach {
            sendSlackGateway.sendMessage(it)
            sqsTemplate.send("product-soldout-check-queue", it)
        }
    }
}
