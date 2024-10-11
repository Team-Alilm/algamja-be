package org.team_alilm.application.port.out

import org.team_alilm.domain.FcmToken
import org.team_alilm.domain.Member

interface LoadFcmTokenPort {

    fun loadFcmTokenAllByMember(member: Member) : List<FcmToken>

}