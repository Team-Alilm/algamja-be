package org.teamalilm.alilmbe.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.teamalilm.alilmbe.adapter.out.persistence.entity.RoleJpaEntity
import org.teamalilm.alilmbe.domain.Role

@Component
class RoleMapper {

    fun mapToJpaEntity (role: Role) : RoleJpaEntity {
        return RoleJpaEntity(
            roleType = role.roleType
        )
    }

}