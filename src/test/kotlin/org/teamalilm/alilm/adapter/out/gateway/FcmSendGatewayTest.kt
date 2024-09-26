package org.teamalilm.alilm.adapter.out.gateway

import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class FcmSendGatewayTest {

    @Autowired
    private lateinit var fcmSendGateway: FcmSendGateway

    @Test
    fun sendFcmMessage() {
        // 실제 FCM 토큰을 여기에 넣으세요
        val actualToken = "fXGMrGRpejrFzmwRLmcch2:APA91bGQ7qyA1a1raHYz58Kjc5GsSICq1w8vkU42CD2WIxyW49GKVjrxUe4hY7H0dbNCQR3k-i0CEglWpJyytRMF_YJbAWspUm7AAquGdJCBcRZedNi4hOO4xmZTs3FAh6dt71ROJD6L"

        val message = Message.builder()
            .setNotification(
                Notification.builder()
                    .setTitle("테스트 알림")
                    .setBody("테스트 메시지가 도착했습니다.")
                    .build()
            )
            .setToken(actualToken)
            .build()

        val response = fcmSendGateway.firebaseMessaging.send(message)

        println("FCM 메시지 전송 결과: $response")
    }
}
