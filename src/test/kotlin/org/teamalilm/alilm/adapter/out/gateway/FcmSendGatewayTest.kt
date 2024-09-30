package org.teamalilm.alilm.adapter.out.gateway

import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@Disabled
@SpringBootTest
class FcmSendGatewayTest {

    @Autowired
    private lateinit var fcmSendGateway: FcmSendGateway

    @Test
    fun sendFcmMessage() {
        // 실제 FCM 토큰을 여기에 넣으세요
        val actualToken = "etll5vw0oRdKviqnC4vZc9:APA91bHqTccME2T5Ob5FJSHddLWky-pLl8JPuZvNg9qc5rAjS4M9j4Kubq7I2CH11yykKFrLAH2nU7Aw60kD18Q654PaYa0VftPgUUpPLB2RggCW9W0HNbTks0XEyfTZgRSAtt2eDrjZ";

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
