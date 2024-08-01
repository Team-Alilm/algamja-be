package org.teamalilm.alilmbe.adapter.out.persistence.adapter

import org.springframework.stereotype.Component
import org.teamalilm.alilmbe.adapter.out.persistence.mapper.RoleMapper
import org.teamalilm.alilmbe.adapter.out.persistence.repository.spring_data.SpringDataRoleRepository
import org.teamalilm.alilmbe.application.port.out.LoadRolePort
import org.teamalilm.alilmbe.domain.Role

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