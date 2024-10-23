package org.team_alilm.adapter.out.persistence.adapter

import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.mapper.AlilmMapper
import org.team_alilm.adapter.out.persistence.repository.spring_data.SpringDataAlilmRepository
import org.team_alilm.application.port.out.AddAlilmPort
import org.team_alilm.domain.Alilm

@Component
class AlilmAdapter(
    private val springDataAlilmRepository: SpringDataAlilmRepository,
    private val alilmMapper: AlilmMapper
) : AddAlilmPort {

    override fun addAlilm(alilm: Alilm) {
        springDataAlilmRepository.save(alilmMapper.mapToJpaEntity(alilm))
    }
}