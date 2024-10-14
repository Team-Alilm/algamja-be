package org.team_alilm.application.port.`in`.use_case

import org.team_alilm.domain.Member

interface LoginMemberUseCase {

    fun loginMember(provider: Member.Provider, providerId: String, attributes: Map<String, Any>): Member
}