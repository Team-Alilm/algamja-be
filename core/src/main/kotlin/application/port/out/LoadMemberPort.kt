package org.team_alilm.application.port.out

import org.team_alilm.domain.Member

interface LoadMemberPort {

    fun loadMember(id: Long): Member?

    fun loadMember(provider: Member.Provider, providerId: String): Member?
}