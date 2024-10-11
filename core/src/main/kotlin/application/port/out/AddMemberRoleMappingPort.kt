package org.team_alilm.application.port.out

import org.team_alilm.domain.Member
import org.team_alilm.domain.Role

interface AddMemberRoleMappingPort {

    fun addMemberRoleMapping(member: Member, role: Role)

}