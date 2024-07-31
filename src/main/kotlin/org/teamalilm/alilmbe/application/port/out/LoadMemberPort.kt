package org.teamalilm.alilmbe.application.port.out

import org.teamalilm.alilmbe.domain.Member

interface LoadMemberPort {

    fun loadMember(phoneNumber: String): Member?

    fun loadMember(id: Long): Member?
}