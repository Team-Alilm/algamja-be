package org.teamalilm.alilmbe.application.port.out

import org.teamalilm.alilmbe.domain.Member

interface AddMemberPort {

    fun addMember(member: Member): Member
}