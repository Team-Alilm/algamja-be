package org.team_alilm.adapter.out.persistence.repository.spring_data

import org.springframework.data.jpa.repository.JpaRepository
import org.team_alilm.adapter.out.persistence.entity.RoleJpaEntity
import org.team_alilm.domain.Role

interface SpringDataRoleRepository : JpaRepository<RoleJpaEntity, Long> {

    fun findByRoleType(roleType: Role.RoleType) : RoleJpaEntity?

}