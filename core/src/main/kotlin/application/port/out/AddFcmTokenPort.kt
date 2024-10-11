package org.team_alilm.application.port.out

import org.team_alilm.domain.FcmToken

interface AddFcmTokenPort {

    fun addFcmToken(fcmToken: FcmToken)

}