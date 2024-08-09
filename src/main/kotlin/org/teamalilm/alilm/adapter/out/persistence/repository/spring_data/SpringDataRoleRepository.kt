package org.teamalilm.alilm.adapter.out.persistence.repository.spring_data

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilm.adapter.out.persistence.entity.RoleJpaEntity
import org.teamalilm.alilm.domain.Role

interface SpringDataRoleRepository : JpaRepository<RoleJpaEntity, Long> {

    fun findByRoleType(roleType: Role.RoleType) : RoleJpaEntity?

}