package org.teamalilm.alilm.adapter.out.persistence.adapter

import org.springframework.stereotype.Component
import org.teamalilm.alilm.adapter.out.persistence.mapper.FcmTokenMapper
import org.teamalilm.alilm.adapter.out.persistence.mapper.MemberMapper
import org.teamalilm.alilm.adapter.out.persistence.repository.spring_data.SpringDataFcmTokenRepository
import org.teamalilm.alilm.application.port.out.AddFcmTokenPort
import org.teamalilm.alilm.domain.FcmToken

@Component
class FcmTokenAdapter(
    val springDataFcmTokenRepository: SpringDataFcmTokenRepository,
    val fcmTokenMapper: FcmTokenMapper,
    val memberMapper: MemberMapper
) : AddFcmTokenPort {

    override fun addFcmToken(fcmToken: FcmToken) {
        val memberJpaEntity = memberMapper.mapToJpaEntity(fcmToken.member)
        val fcmTokenJpaEntity = fcmTokenMapper.mapToJpaEntity(fcmToken, memberJpaEntity)

        springDataFcmTokenRepository.save(fcmTokenJpaEntity)
    }

}