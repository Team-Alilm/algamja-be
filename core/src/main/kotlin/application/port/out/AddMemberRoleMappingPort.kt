package org.team_alilm.application.port.out

import domain.Member
import domain.Role

interface AddMemberRoleMappingPort {

    fun addMemberRoleMapping(member: Member, role: Role)

}