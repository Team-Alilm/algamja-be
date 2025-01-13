package org.team_alilm.application.port.out

import domain.FcmToken

interface AddFcmTokenPort {

    fun addFcmToken(fcmToken: FcmToken)

}