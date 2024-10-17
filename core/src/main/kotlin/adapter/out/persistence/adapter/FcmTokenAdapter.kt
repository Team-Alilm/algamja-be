package org.team_alilm.adapter.out.persistence.adapter

import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.mapper.FcmTokenMapper
import org.team_alilm.adapter.out.persistence.repository.spring_data.SpringDataFcmTokenRepository
import org.team_alilm.application.port.out.AddFcmTokenPort
import org.team_alilm.application.port.out.LoadFcmTokenPort
import org.team_alilm.domain.FcmToken

@Component
class FcmTokenAdapter(
    val fcmTokenMapper: FcmTokenMapper,
    private val springDataFcmTokenRepository: SpringDataFcmTokenRepository
) : AddFcmTokenPort, LoadFcmTokenPort {

    override fun addFcmToken(fcmToken: FcmToken) {
        val fcmTokenJpaEntity = fcmTokenMapper.mapToJpaEntity(
            fcmToken = fcmToken,
            memberJpaEntityId = fcmToken.memberId.value
        )
        springDataFcmTokenRepository.save(fcmTokenJpaEntity)
    }

    override fun loadFcmTokenAllByMember(memberId: Long): List<FcmToken> {
        val fcmJpaEntityList = springDataFcmTokenRepository.findByMemberJpaEntityId(memberId)

        return fcmJpaEntityList.map { fcmTokenMapper.mapToDomain(it) }
    }

}