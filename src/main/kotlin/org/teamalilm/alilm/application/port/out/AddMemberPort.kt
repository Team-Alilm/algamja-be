package org.teamalilm.alilm.application.port.out

import org.teamalilm.alilm.domain.Member

interface AddMemberPort {

    fun addMember(member: Member): Member

}