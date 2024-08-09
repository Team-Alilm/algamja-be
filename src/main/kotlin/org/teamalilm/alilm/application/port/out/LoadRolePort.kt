package org.teamalilm.alilm.application.port.out

import org.teamalilm.alilm.domain.Role
import org.teamalilm.alilm.domain.Role.*

interface LoadRolePort {

    fun loadRole(roleType: RoleType) : Role?

}