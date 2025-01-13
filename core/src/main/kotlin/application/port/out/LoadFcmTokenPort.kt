package org.team_alilm.application.port.out

import domain.FcmToken
import domain.Member

interface LoadFcmTokenPort {

    fun loadFcmTokenAllByMember(memberId: Long) : List<FcmToken>
    fun loadFcmToken(token: String): FcmToken?

}