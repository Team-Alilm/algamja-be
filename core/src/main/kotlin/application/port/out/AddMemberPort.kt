package org.team_alilm.application.port.out

import org.team_alilm.domain.Member

interface AddMemberPort {

    fun addMember(member: Member): Member

}