package org.teamalilm.alilm.adapter.out.persistence.adapter

import org.springframework.stereotype.Component
import org.teamalilm.alilm.adapter.out.persistence.mapper.MemberMapper
import org.teamalilm.alilm.adapter.out.persistence.mapper.MemberRoleMappingMapper
import org.teamalilm.alilm.adapter.out.persistence.mapper.RoleMapper
import org.teamalilm.alilm.adapter.out.persistence.repository.spring_data.SpringDataMemberRoleMappingRepository
import org.teamalilm.alilm.application.port.out.AddMemberRoleMappingPort
import org.teamalilm.alilm.domain.Member
import org.teamalilm.alilm.domain.Role

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