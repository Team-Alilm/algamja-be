package org.team_alilm.adapter.out.persistence.adapter

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.mapper.MemberMapper
import org.team_alilm.adapter.out.persistence.repository.spring_data.SpringDataMemberRepository
import org.team_alilm.application.port.out.AddMemberPort
import org.team_alilm.application.port.out.LoadMemberPort
import org.team_alilm.domain.Member

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

    override fun loadMember(provider: Member.Provider, providerId: String): Member? {
        return memberMapper.mapToDomainEntityOrNull(
            springDataMemberRepository.findByIsDeleteFalseAndProviderAndProviderId(provider, providerId.toLong())
        )
    }

    override fun loadMemberCount(): Long {
        return springDataMemberRepository.count()
    }

    override fun addMember(member: Member): Member {
        return memberMapper.mapToDomainEntity(
            springDataMemberRepository.save(
                memberMapper.mapToJpaEntity(member)
            )
        )
    }

}