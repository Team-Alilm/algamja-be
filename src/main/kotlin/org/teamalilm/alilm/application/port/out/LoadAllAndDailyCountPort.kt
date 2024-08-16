package org.teamalilm.alilm.application.port.out

interface LoadAllAndDailyCountPort {

    fun getAllAndDailyCount() : AllAndDailyCount

    data class AllAndDailyCount(
        val allCount : Long,
        val dailyCount : Long
    )

}