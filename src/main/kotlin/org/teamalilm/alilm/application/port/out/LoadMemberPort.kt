package org.teamalilm.alilm.application.port.out

import org.teamalilm.alilm.domain.Member

interface LoadMemberPort {

    fun loadMember(phoneNumber: String): Member?

    fun loadMember(id: Long): Member?

    fun loadMember(provider: String, providerId: String): Member?
}