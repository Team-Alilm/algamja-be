package org.teamalilm.alilmbe.adapter.out.persistence.adapter

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.teamalilm.alilmbe.adapter.out.persistence.mapper.MemberMapper
import org.teamalilm.alilmbe.adapter.out.persistence.repository.spring_data.SpringDataMemberRepository
import org.teamalilm.alilmbe.application.port.out.AddMemberPort
import org.teamalilm.alilmbe.application.port.out.LoadMemberPort
import org.teamalilm.alilmbe.domain.Member

@Component
class MemberPersistenceAdapter (
    private val springDataMemberRepository: SpringDataMemberRepository,
    private val memberMapper: MemberMapper
) : LoadMemberPort, AddMemberPort {

    override fun loadMember(phoneNumber: String): Member? {
        return memberMapper.mapToDomainEntityOrNull(
            springDataMemberRepository.findByPhoneNumberAndIsDeleteFalse(phoneNumber)
        )
    }

    override fun loadMember(id: Long): Member? {
        return memberMapper.mapToDomainEntityOrNull(
            springDataMemberRepository.findByIdOrNull(id)
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