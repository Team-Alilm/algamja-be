package org.team_alilm.adapter.out.persistence.adapter

import domain.FcmToken
import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.mapper.FcmTokenMapper
import org.team_alilm.adapter.out.persistence.repository.spring_data.SpringDataFcmTokenRepository
import org.team_alilm.application.port.out.AddFcmTokenPort
import org.team_alilm.application.port.out.LoadFcmTokenPort

@Component
class FcmTokenAdapter(
    val fcmTokenMapper: FcmTokenMapper,
    private val springDataFcmTokenRepository: SpringDataFcmTokenRepository
) : AddFcmTokenPort, LoadFcmTokenPort {

    override fun addFcmToken(fcmToken: FcmToken) {
        val fcmTokenJpaEntity = fcmTokenMapper.mapToJpaEntity(
            fcmToken = fcmToken,
            memberId = fcmToken.memberId.value
        )
        springDataFcmTokenRepository.save(fcmTokenJpaEntity)
    }

    override fun loadFcmTokenAllByMember(memberId: Long): List<FcmToken> {
        val fcmJpaEntityList = springDataFcmTokenRepository.findByMemberId(memberId)

        return fcmJpaEntityList.map { fcmTokenMapper.mapToDomain(it) }
    }

    override fun loadFcmToken(token: String): FcmToken? {
        val fcmJpaEntity = springDataFcmTokenRepository.findByToken(token) ?: return null

        return fcmTokenMapper.mapToDomain(fcmJpaEntity)
    }

}