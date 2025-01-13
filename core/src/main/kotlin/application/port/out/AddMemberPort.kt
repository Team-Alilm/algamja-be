package org.team_alilm.application.port.out

import domain.Member

interface AddMemberPort {

    fun addMember(member: Member): Member

}