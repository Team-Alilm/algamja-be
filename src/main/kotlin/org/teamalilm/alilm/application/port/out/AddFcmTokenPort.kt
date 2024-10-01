package org.teamalilm.alilm.application.port.out

import org.teamalilm.alilm.domain.FcmToken

interface AddFcmTokenPort {

    fun addFcmToken(fcmToken: FcmToken)

}