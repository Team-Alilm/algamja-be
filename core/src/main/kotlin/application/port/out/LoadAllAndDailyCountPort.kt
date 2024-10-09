package org.team_alilm.application.port.out

interface LoadAllAndDailyCountPort {

    fun getAllAndDailyCount() : org.team_alilm.application.port.out.LoadAllAndDailyCountPort.AllAndDailyCount

    data class AllAndDailyCount(
        val allCount : Long,
        val dailyCount : Long
    )

}