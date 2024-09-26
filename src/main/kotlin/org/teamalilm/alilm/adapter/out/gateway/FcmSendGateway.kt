package org.teamalilm.alilm.adapter.out.gateway

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class FcmSendGateway(
    val firebaseMessaging: FirebaseMessaging
) {

    private val log = LoggerFactory.getLogger(FcmSendGateway::class.java)

    fun sendFcmMessage() {
        // send fcm message
        val message = firebaseMessaging.send(Message.builder()
            .setNotification(
                Notification.builder()
                    .setTitle("Alilm")
                    .setBody("알림이 왔어요!")
                    .build()
                ).setToken("token")
            .build()
        )

        log.info("Successfully sent message: $message")
    }

}