package org.teamalilm.alilm.adapter.out.gateway

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.teamalilm.alilm.domain.FcmToken
import org.teamalilm.alilm.domain.Member
import org.teamalilm.alilm.domain.Product

@Service
class FcmSendGateway(
    val firebaseMessaging: FirebaseMessaging
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

        val imageUrl = "https://alilm.store/alilm.png"

        // FCM 메시지 구성
        val message = firebaseMessaging.send(
            Message.builder()
                .setNotification(
                    Notification.builder()
                        .setTitle("알림 : 등록 하신 상품이 재 입고 되었어요.")
                        .setBody("""
                            ${product.name} 상품이 재입고 되었습니다.
                            ${if (options.isNotBlank()) "option : $options" else ""}
                           
                            바로 확인해보세요!
                            https://www.musinsa.com/products/${product.number}
                        """.trimIndent())
                        .setImage(imageUrl)
                        .build()
                )
                .setToken(fcmToken.token)  // 실제 FCM 토큰을 설정해야 함
                .build()
        )

        log.info("Successfully sent message: $message")
    }
}
