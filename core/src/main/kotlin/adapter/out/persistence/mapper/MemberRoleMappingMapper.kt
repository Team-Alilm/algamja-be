package org.team_alilm.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.teamalilm.alilm.adapter.out.persistence.entity.MemberJpaEntity
import org.teamalilm.alilm.adapter.out.persistence.entity.MemberRoleMappingJpaEntity
import org.teamalilm.alilm.adapter.out.persistence.entity.RoleJpaEntity

@Component
class MemberRoleMappingMapper {

    fun mapToJpaEntity(memberJpaEntity: MemberJpaEntity, roleJpaEntity: RoleJpaEntity) : MemberRoleMappingJpaEntity {
        return MemberRoleMappingJpaEntity(
            memberJpaEntity = memberJpaEntity,
            roleJpaEntity = roleJpaEntity
        )
    }

}