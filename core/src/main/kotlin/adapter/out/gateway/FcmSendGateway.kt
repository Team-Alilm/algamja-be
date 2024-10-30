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
                    .setImage(product.imageUrl)
                    // [유니폼 브릿지 야상] 상품이 재 입고 되었습니다!
                    .setTitle("[${product.name}] 상품이 재 입고 되었습니다!")
                    .setBody("""
                        ${if (options.isNotBlank()) "option : $options" else ""}
                        지금 바로 확인해보세요. server에서 발송하는 fcm입니다.
                    """.trimIndent())
                    .build()
                )

                .setToken(fcmToken.token)
                .build()

        firebaseMessaging.send(message)

        log.info("Successfully sent message: $message")
    }
}
