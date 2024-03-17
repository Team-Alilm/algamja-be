package org.teamalilm.alilmbe.domain.tracer

import org.quartz.Job
import org.quartz.JobBuilder
import org.quartz.JobExecutionContext
import org.quartz.SchedulerException
import org.quartz.SimpleScheduleBuilder
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.teamalilm.alilmbe.domain.basket.repository.BasketRepository
import org.teamalilm.alilmbe.domain.product.repository.ProductRepository
import java.net.URI

class SoldoutTracer(
    val productRepository: ProductRepository,
    val basketRepository: BasketRepository
) {

    private val log = LoggerFactory.getLogger(SoldoutTracer::class.java)

    @Throws(SchedulerException::class)
    fun startTracing() {
        val scheduler = StdSchedulerFactory.getDefaultScheduler()
        scheduler.start()

        val job = JobBuilder.newJob(SoldoutCheckJob::class.java)
            .withIdentity("soldoutCheckJob", "soldoutTracer")
            .build()

        val trigger = TriggerBuilder.newTrigger()
            .withIdentity("soldoutCheckTrigger", "soldoutTracer")
            .startNow()
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(5)
                    .repeatForever()
            )
            .build()

        scheduler.scheduleJob(job, trigger)
    }

    inner class SoldoutCheckJob : Job {
        override fun execute(context: JobExecutionContext) {
            log.info("SoldoutCheckJob is running")

            val soldoutProductIds = mutableListOf<Long>()
            val restClient = RestClient.create()
            val products = productRepository.findAllByOrderByCreatedDateDesc()

            products.forEach {
                val apiUrl = API_URL_TEMPLATE.format(it.productInfo.number)
                val response = restClient.get()
                    .uri(URI.create(apiUrl))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body<SoldoutCheckResponse>()

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

    companion object {
        const val API_URL_TEMPLATE = "https://goods-detail.musinsa.com/goods/%s/options?goodsSaleType=SALE"
    }

    data class SoldoutCheckResponse(
        val data: Data
    ) {

        data class Data(
            val basic: List<BasicOption>,
        ) {

            data class BasicOption(
                val name: String,
                val price: Int,
                val isSoldOut: Boolean,
                val remainQuantity: Int,
                val subOptions: List<SubOption>  // SubOptions가 정확한 데이터 형식을 알 수 없어서 일단 Any로 정의
            ) {

                data class SubOption(
                    val name: String,
                    val price: Int,
                    val isSoldOut: Boolean,
                    val remainQuantity: Int,
                )
            }
        }
    }
}