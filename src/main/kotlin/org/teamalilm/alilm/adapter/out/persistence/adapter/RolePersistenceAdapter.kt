package org.teamalilm.alilm.adapter.out.persistence.adapter

import org.springframework.stereotype.Component
import org.teamalilm.alilm.adapter.out.persistence.mapper.RoleMapper
import org.teamalilm.alilm.adapter.out.persistence.repository.spring_data.SpringDataRoleRepository
import org.teamalilm.alilm.application.port.out.LoadRolePort
import org.teamalilm.alilm.domain.Role

@Component
class RolePersistenceAdapter(
    val springDataRoleRepository: SpringDataRoleRepository,
    val roleMapper: RoleMapper
) : LoadRolePort {

    override fun loadRole(roleType: Role.RoleType) : Role? {
        return roleMapper.mapToDomainEntityOrNull(
            springDataRoleRepository.findByRoleType(roleType)
        )
    }
}