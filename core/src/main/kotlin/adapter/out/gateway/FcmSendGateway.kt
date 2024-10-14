package org.team_alilm.adapter.out.gateway

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.team_alilm.domain.FcmToken
import org.team_alilm.domain.Member
import org.team_alilm.domain.Product

@Service
class FcmSendGateway(
    private val firebaseMessaging: FirebaseMessaging
) {

    private val log = LoggerFactory.getLogger(FcmSendGateway::class.java)

    fun sendFcmMessage(
        member: Member,
        product: Product,
        fcmToken: FcmToken
    ) {
        // 옵션들 중 null이 아닌 값을 필터링하여 메시지에 포함
        val options = listOfNotNull(product.firstOption, product.secondOption, product.thirdOption)
            .joinToString(" / ")

        // FCM 메시지 구성
        val message = Message.builder()
            // Notification 메시지에 이미지 포함
            .setNotification(
                Notification.builder()
                    .setTitle("알림 : 등록 하신 상품이 재 입고 되었어요.")
                    .setBody("""
                        ${product.name} 상품이 재입고 되었습니다.
                        ${if (options.isNotBlank()) "option : $options" else ""}
                
                        바로 확인해보세요!
                    """.trimIndent())
                    .build()
                )

                // 데이터 메시지에 클릭 링크 추가
                .putData("click_action", "https://www.musinsa.com/products/${product.number}")
                .setToken(fcmToken.token)
                .build()

        firebaseMessaging.send(message)

        log.info("Successfully sent message: $message")
    }
}
