package org.team_alilm.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.entity.MemberJpaEntity
import org.team_alilm.adapter.out.persistence.entity.MemberRoleMappingJpaEntity
import org.team_alilm.adapter.out.persistence.entity.RoleJpaEntity

@Component
class MemberRoleMappingMapper {

    fun mapToJpaEntity(memberJpaEntityId: Long, roleJpaEntityId: Long) : MemberRoleMappingJpaEntity {
        return MemberRoleMappingJpaEntity(
            memberId = memberJpaEntityId,
            roleId = roleJpaEntityId
        )
    }

}