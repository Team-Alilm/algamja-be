package org.teamalilm.alilm.common.config

import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.teamalilm.alilm.adapter.out.persistence.entity.RoleJpaEntity
import org.teamalilm.alilm.adapter.out.persistence.repository.spring_data.SpringDataRoleRepository
import org.teamalilm.alilm.domain.Role

@Component
class DataInitializer(
    private val springDataRoleRepository: SpringDataRoleRepository
) {

    @EventListener(ContextRefreshedEvent::class)
    fun init() {
        val roles = listOf(
            RoleJpaEntity(
                id = 1L,
                roleType = Role.RoleType.ROLE_USER,
            ),
            RoleJpaEntity(
                id = 2L,
                roleType = Role.RoleType.ROLE_ADMIN,
            ),
            RoleJpaEntity(
                id = 3L,
                roleType = Role.RoleType.ROLE_GUEST,
            ),
        )

        if (springDataRoleRepository.count() == 0L) {
            springDataRoleRepository.saveAll(roles)
        }
    }
}