package org.team_alilm.application.port.out

import org.team_alilm.domain.Role
import org.team_alilm.domain.Role.*

interface LoadRolePort {

    fun loadRole(roleType: RoleType) : Role?

}