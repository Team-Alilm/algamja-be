package org.teamalilm.alilmbe.adapter.out.persistence.adapter

import org.springframework.stereotype.Component
import org.teamalilm.alilmbe.adapter.out.persistence.mapper.MemberMapper
import org.teamalilm.alilmbe.adapter.out.persistence.mapper.MemberRoleMappingMapper
import org.teamalilm.alilmbe.adapter.out.persistence.mapper.RoleMapper
import org.teamalilm.alilmbe.adapter.out.persistence.repository.spring_data.SpringDataMemberRoleMappingRepository
import org.teamalilm.alilmbe.application.port.out.AddMemberRoleMappingPort
import org.teamalilm.alilmbe.domain.Member
import org.teamalilm.alilmbe.domain.Role

@Component
class MemberRoleMappingPersistenceAdapter(
    val springDataMemberRoleMappingRepository: SpringDataMemberRoleMappingRepository,
    val memberRoleMappingMapper: MemberRoleMappingMapper,
    val memberMapper: MemberMapper,
    val roleMapper: RoleMapper
) : AddMemberRoleMappingPort {

    override fun addMemberRoleMapping(member: Member, role: Role) {
        springDataMemberRoleMappingRepository.save(
            memberRoleMappingMapper.mapToJpaEntity(
                memberMapper.mapToJpaEntity(member),
                roleMapper.mapToJpaEntity(role)
            )
        )
    }

}