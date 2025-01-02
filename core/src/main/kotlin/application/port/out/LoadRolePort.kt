package org.team_alilm.application.port.out

import domain.Role

interface LoadRolePort {

    fun loadRole(roleType: Role.RoleType) : Role?

}