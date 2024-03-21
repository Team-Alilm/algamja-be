package org.teamalilm.alilmbe.domain.tracer

import org.quartz.JobBuilder
import org.quartz.Scheduler
import org.quartz.SchedulerException
import org.quartz.SimpleScheduleBuilder
import org.quartz.TriggerBuilder

/**
 *  SoldoutScheduler
 *
 *  @author jubi
 *  @version 1.0.0
 *  @date 2024-03-21
 **/
class SoldoutScheduler(
    val scheduler: Scheduler
) {

    @Throws(SchedulerException::class)
    fun startTracing() {
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

    companion object {
        const val API_URL_TEMPLATE =
            "https://goods-detail.musinsa.com/goods/%s/options?goodsSaleType=SALE"
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