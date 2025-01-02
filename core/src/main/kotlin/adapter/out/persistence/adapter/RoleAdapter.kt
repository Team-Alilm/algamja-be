package org.team_alilm.adapter.out.persistence.adapter

import domain.Role
import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.mapper.RoleMapper
import org.team_alilm.adapter.out.persistence.repository.spring_data.SpringDataRoleRepository
import org.team_alilm.application.port.out.LoadRolePort

@Component
class RoleAdapter(
    val springDataRoleRepository: SpringDataRoleRepository,
    val roleMapper: RoleMapper
) : LoadRolePort {

    override fun loadRole(roleType: Role.RoleType) : Role? {
        return roleMapper.mapToDomainEntityOrNull(
            springDataRoleRepository.findByRoleType(roleType)
        )
    }
}