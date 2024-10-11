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
    val springDataFcmTokenRepository: SpringDataFcmTokenRepository,
    val fcmTokenMapper: FcmTokenMapper,
    val memberMapper: MemberMapper,
    private val springDataMemberRepository: SpringDataMemberRepository
) : AddFcmTokenPort, LoadFcmTokenPort {

    override fun addFcmToken(fcmToken: FcmToken) {
        val memberJpaEntity = springDataMemberRepository.findByIdOrNull(fcmToken.memberId.value)
            ?: error("Member ID is null")
        val fcmTokenJpaEntity = fcmTokenMapper.mapToJpaEntity(fcmToken, memberJpaEntity)

        springDataFcmTokenRepository.save(fcmTokenJpaEntity)
    }

    override fun loadFcmTokenAllByMember(member: Member): List<FcmToken> {
        val memberJpaEntity = memberMapper.mapToJpaEntity(member)

        val fcmJpaEntityList = springDataFcmTokenRepository.findAllByMember(memberJpaEntity)

        return fcmJpaEntityList.map { fcmTokenMapper.mapToDomain(it) }
    }

}