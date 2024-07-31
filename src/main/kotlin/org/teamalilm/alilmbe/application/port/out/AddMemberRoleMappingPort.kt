package org.teamalilm.alilmbe.application.port.out

import org.teamalilm.alilmbe.domain.Member
import org.teamalilm.alilmbe.domain.Role

interface AddMemberRoleMappingPort {

    fun addMemberRoleMapping(member: Member, role: Role)

}