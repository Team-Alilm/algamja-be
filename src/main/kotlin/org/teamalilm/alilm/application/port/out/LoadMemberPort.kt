package org.teamalilm.alilm.application.port.out

import org.teamalilm.alilm.domain.Member
import org.teamalilm.alilm.global.security.service.oAuth2.data.Provider

interface LoadMemberPort {

    fun loadMember(phoneNumber: String): Member?

    fun loadMember(id: Long): Member?

    fun loadMember(provider: Provider, providerId: String): Member?
}