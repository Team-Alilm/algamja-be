package org.team_alilm.application.port.out

import domain.Member

interface LoadMemberPort {

    fun loadMember(id: Long): Member?

    fun loadMember(provider: Member.Provider, providerId: String): Member?

    fun loadMemberCount(): Long
}