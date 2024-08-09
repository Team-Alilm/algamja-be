package org.teamalilm.alilm.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.teamalilm.alilm.adapter.out.persistence.entity.RoleJpaEntity
import org.teamalilm.alilm.domain.Role

@Component
class RoleMapper {

    fun mapToJpaEntity (role: Role) : RoleJpaEntity {
        return RoleJpaEntity(
            id = role.id?.value,
            roleType = role.roleType
        )
    }

    fun mapToDomainEntityOrNull(roleJpaEntity: RoleJpaEntity?) : Role? {
        roleJpaEntity ?: return null

        return Role(
            roleType = roleJpaEntity.roleType,
            id = Role.RoleId(roleJpaEntity.id!!)
        )
    }
}