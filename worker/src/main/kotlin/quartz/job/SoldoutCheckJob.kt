package org.team_alilm.quartz.job

import kotlinx.coroutines.*
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.adapter.out.gateway.SlackGateway
import org.team_alilm.application.port.out.*
import org.team_alilm.quartz.job.handler.PlatformHandlerResolver

@Component
@Transactional(readOnly = true)
class SoldoutCheckJob(
    private val loadCrawlingProductsPort: LoadCrawlingProductsPort,
    private val coroutineScope: CoroutineScope,
    private val platformHandlerResolver: PlatformHandlerResolver,
    private val slackGateway: SlackGateway
) : Job {

    private val log = LoggerFactory.getLogger(SoldoutCheckJob::class.java)

    @Transactional
    override fun execute(context: JobExecutionContext) {
        log.info("SoldoutCheckJob started")

        val productAndMembersList = loadCrawlingProductsPort.loadCrawlingProducts()

        log.info("ProductAndMembersList size: ${productAndMembersList.size}")

        // 비동기 작업으로 전환해요.
        coroutineScope.launch {
            productAndMembersList.chunked(10).forEach { productAndMembers -> // 병렬 처리 수 제한
                launch {
                    productAndMembers.forEach {
                        try {
                            platformHandlerResolver
                                .resolve(it.product.store)
                                .process(it)
                        } catch (e: Exception) {
                            log.info("Error processing product ${it.product.id}: ${e.message}")
                            slackGateway.sendMessage("Error processing productId: ${it.product.id}: ${e.message}")
                        }
                    }
                }
            }
        }
    }
}