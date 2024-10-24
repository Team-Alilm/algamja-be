package org.team_alilm.adapter.out.persistence.adapter

import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.mapper.MemberMapper
import org.team_alilm.adapter.out.persistence.mapper.MemberRoleMappingMapper
import org.team_alilm.adapter.out.persistence.mapper.RoleMapper
import org.team_alilm.adapter.out.persistence.repository.spring_data.SpringDataMemberRoleMappingRepository
import org.team_alilm.application.port.out.AddMemberRoleMappingPort
import org.team_alilm.domain.Member
import org.team_alilm.domain.Role
import org.team_alilm.global.error.NotFoundMemberException
import org.team_alilm.global.error.NotFoundRoleException

@Component
class MemberRoleMappingAdapter(
    val springDataMemberRoleMappingRepository: SpringDataMemberRoleMappingRepository,
    val memberRoleMappingMapper: MemberRoleMappingMapper,
    val memberMapper: MemberMapper,
    val roleMapper: RoleMapper
) : AddMemberRoleMappingPort {

    override fun addMemberRoleMapping(member: Member, role: Role) {
        springDataMemberRoleMappingRepository.save(
            memberRoleMappingMapper.mapToJpaEntity(
                member.id?.value ?: throw NotFoundMemberException(),
                role.id?.value ?: throw NotFoundRoleException()
            )
        )
    }

}
