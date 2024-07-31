package org.teamalilm.alilmbe.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.teamalilm.alilmbe.adapter.out.persistence.entity.MemberJpaEntity
import org.teamalilm.alilmbe.adapter.out.persistence.entity.MemberRoleMappingJpaEntity
import org.teamalilm.alilmbe.adapter.out.persistence.entity.RoleJpaEntity

@Component
class MemberRoleMappingMapper {

    fun mapToJpaEntity(memberJpaEntity: MemberJpaEntity, roleJpaEntity: RoleJpaEntity) : MemberRoleMappingJpaEntity {
        return MemberRoleMappingJpaEntity(
            memberJpaEntity = memberJpaEntity,
            roleJpaEntity = roleJpaEntity
        )
    }

}