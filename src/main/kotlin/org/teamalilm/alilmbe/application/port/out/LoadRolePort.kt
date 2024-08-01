package org.teamalilm.alilmbe.application.port.out

import org.teamalilm.alilmbe.domain.Role
import org.teamalilm.alilmbe.domain.Role.*

interface LoadRolePort {

    fun loadRole(roleType: RoleType) : Role?

}