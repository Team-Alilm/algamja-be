package org.team_alilm.application.port.out

import org.teamalilm.alilm.domain.Member
import org.teamalilm.alilm.domain.Role

interface AddMemberRoleMappingPort {

    fun addMemberRoleMapping(member: Member, role: Role)

}