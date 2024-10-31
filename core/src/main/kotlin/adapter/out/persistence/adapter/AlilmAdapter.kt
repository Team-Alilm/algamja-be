package org.team_alilm.adapter.out.persistence.adapter

import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.mapper.AlilmMapper
import org.team_alilm.adapter.out.persistence.repository.AlilmRepository
import org.team_alilm.adapter.out.persistence.repository.spring_data.SpringDataAlilmRepository
import org.team_alilm.application.port.out.AddAlilmPort
import org.team_alilm.application.port.out.LoadAllAndDailyCountPort
import org.team_alilm.domain.Alilm
import java.time.LocalDate
import java.time.ZoneOffset

@Component
class AlilmAdapter(
    private val springDataAlilmRepository: SpringDataAlilmRepository,
    private val alilmRepository: AlilmRepository,
    private val alilmMapper: AlilmMapper
) : AddAlilmPort,
    LoadAllAndDailyCountPort {

    override fun addAlilm(alilm: Alilm) {
        springDataAlilmRepository.save(alilmMapper.mapToJpaEntity(alilm))
    }

    override fun getAllAndDailyCount(): LoadAllAndDailyCountPort.AllAndDailyCount {
        val midnightMillis: Long = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val allCount = alilmRepository.allCountAndDailyCount(midnightMillis)
        return LoadAllAndDailyCountPort.AllAndDailyCount(
            allCount = allCount.allCount,
            dailyCount = allCount.dailyCount
        )
    }
}