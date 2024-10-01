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
        val actualToken = "fpAvwbzb-HNpwBb_odzt9c:APA91bF04uKrm0YYlx5Pj0tnJs_XXAYmPES6XOQrjprHoZpe8T1i0WDAoZxGasgYNH12LZKbCX8OhYTEjyfOb5vyNoHN95Z49FJ4tC0DfH7IjHYt0Ju3n0l8-HVRbQ77uZh1LpHAhJgH";

        val message = Message.builder()
            .setNotification(
                Notification.builder()
                    .setTitle("테스트 알림")
                    .setBody("테스트 메시지가 도착했습니다.")
                    .setImage("https://alilm.store/alilm.png")
                    .build()
            )
            .setToken(actualToken)
            .build()

//        val response = fcmSendGateway.sendFcmMessage(message)

//        println("FCM 메시지 전송 결과: $response")
    }
}
