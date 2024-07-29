package org.teamalilm.alilmbe.application.port.out.member

import org.teamalilm.alilmbe.domain.member.Member

interface AddMemberPort {

    fun addMember(member: Member): Member
}