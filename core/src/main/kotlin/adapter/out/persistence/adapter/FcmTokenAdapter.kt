package org.team_alilm.adapter.out.persistence.adapter

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.mapper.FcmTokenMapper
import org.team_alilm.adapter.out.persistence.mapper.MemberMapper
import org.team_alilm.adapter.out.persistence.repository.spring_data.SpringDataFcmTokenRepository
import org.team_alilm.adapter.out.persistence.repository.spring_data.SpringDataMemberRepository
import org.team_alilm.application.port.out.AddFcmTokenPort
import org.team_alilm.application.port.out.LoadFcmTokenPort
import org.team_alilm.domain.FcmToken
import org.team_alilm.domain.Member

@Component
class FcmTokenAdapter(
    val fcmTokenMapper: FcmTokenMapper,
    val memberMapper: MemberMapper,
    private val springDataFcmTokenRepository: SpringDataFcmTokenRepository
) : AddFcmTokenPort, LoadFcmTokenPort {

    override fun addFcmToken(fcmToken: FcmToken) {
        val fcmTokenJpaEntity = fcmTokenMapper.mapToJpaEntity(fcmToken, fcmToken.id!!.value)
        springDataFcmTokenRepository.save(fcmTokenJpaEntity)
    }

    override fun loadFcmTokenAllByMember(member: Member): List<FcmToken> {
        val memberJpaEntity = memberMapper.mapToJpaEntity(member)
        val fcmJpaEntityList = springDataFcmTokenRepository.findByMemberJpaEntityId(memberJpaEntity.id!!)

        return fcmJpaEntityList.map { fcmTokenMapper.mapToDomain(it) }
    }

}