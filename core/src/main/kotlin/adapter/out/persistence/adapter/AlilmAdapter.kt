package org.team_alilm.adapter.out.persistence.adapter

import domain.Alilm
import domain.product.Product
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.mapper.AlilmMapper
import org.team_alilm.adapter.out.persistence.mapper.ProductMapper
import org.team_alilm.adapter.out.persistence.repository.AlilmRepository
import org.team_alilm.adapter.out.persistence.repository.spring_data.SpringDataAlilmRepository
import org.team_alilm.application.port.out.AddAlilmPort
import org.team_alilm.application.port.out.LoadAlilmPort
import org.team_alilm.application.port.out.LoadAllAndDailyCountPort
import java.time.LocalDate
import java.time.ZoneOffset

@Component
class AlilmAdapter(
    private val springDataAlilmRepository: SpringDataAlilmRepository,
    private val alilmRepository: AlilmRepository,
    private val alilmMapper: AlilmMapper,
    private val productMapper: ProductMapper
) : AddAlilmPort,
    LoadAllAndDailyCountPort,
    LoadAlilmPort
{

    override fun addAlilm(alilm: Alilm) {
        springDataAlilmRepository.save(alilmMapper.mapToJpaEntity(alilm))
    }

    override fun getAllAndDailyCount(): LoadAllAndDailyCountPort.AllAndDailyCount {
        val midnightMillis = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

        val allCount = alilmRepository.allCountAndDailyCount(midnightMillis)
        return LoadAllAndDailyCountPort.AllAndDailyCount(
            allCount = allCount.allCount,
            dailyCount = allCount.dailyCount
        )
    }

    override fun loadAlilm(count: Int): List<Product> {
        return alilmRepository.findByRestockRanking(count = count).map { productMapper.mapToDomainEntity(it) }
    }

}