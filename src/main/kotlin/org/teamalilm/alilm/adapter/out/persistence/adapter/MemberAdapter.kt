package org.teamalilm.alilm.adapter.out.persistence.adapter

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.teamalilm.alilm.adapter.out.persistence.mapper.MemberMapper
import org.teamalilm.alilm.adapter.out.persistence.repository.spring_data.SpringDataMemberRepository
import org.teamalilm.alilm.application.port.out.AddMemberPort
import org.teamalilm.alilm.application.port.out.LoadMemberPort
import org.teamalilm.alilm.domain.Member
import org.teamalilm.alilm.global.security.service.oAuth2.data.Provider

@Component
class MemberAdapter (
    private val springDataMemberRepository: SpringDataMemberRepository,
    private val memberMapper: MemberMapper
) : LoadMemberPort, AddMemberPort {

    override fun loadMember(id: Long): Member? {
        return memberMapper.mapToDomainEntityOrNull(
            springDataMemberRepository.findByIdOrNull(id)
        )
    }

    override fun loadMember(provider: Provider, providerId: String): Member? {
        return memberMapper.mapToDomainEntityOrNull(
            springDataMemberRepository.findByIsDeleteFalseAndProviderAndProviderId(provider, providerId.toLong())
        )
    }

    override fun addMember(member: Member): Member {
        return memberMapper.mapToDomainEntity(
            springDataMemberRepository.save(
                memberMapper.mapToJpaEntity(member)
            )
        )
    }

}