package org.teamalilm.alilm.application.port.out

import org.teamalilm.alilm.domain.FcmToken
import org.teamalilm.alilm.domain.Member

interface LoadFcmTokenPort {

    fun loadFcmTokenAllByMember(member: Member) : List<FcmToken>

}