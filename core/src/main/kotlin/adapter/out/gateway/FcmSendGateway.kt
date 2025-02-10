package org.team_alilm.adapter.out.gateway

import com.google.firebase.messaging.*
import domain.FcmToken
import domain.Member
import domain.product.Product
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

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
        val title = "[${product.name}] 상품이 재 입고 되었습니다!"
        val options = listOfNotNull(product.firstOption, product.secondOption, product.thirdOption)
            .joinToString(" / ")
        val body = """
            ${if (options.isNotBlank()) "option : $options" else ""}
            지금 바로 확인해보세요.
        """.trimIndent()

        // FCM 메시지 구성 (Data 메시지로만)
        val message = Message.builder()
            .setNotification(
                Notification.builder()
                    .setTitle("[${product.name}] 상품이 재 입고 되었습니다!")
                    .setBody("""
                        ${if (options.isNotBlank()) "option : $options" else ""}
                        지금 바로 확인해보세요.
                    """.trimIndent())
                    .setImage(product.thumbnailUrl)
                    .build()
            )
            .setAndroidConfig(
                AndroidConfig.builder()
                    .setNotification(
                        AndroidNotification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .setImage(product.thumbnailUrl)
                            .setClickAction("push_click")
                            .build()
                    )
                    .build()
            )
            .setApnsConfig(
                ApnsConfig.builder()
                    .setAps(
                        Aps.builder()
                            .setCategory("push_click")
                            .setAlert(
                                ApsAlert.builder()
                                    .setLaunchImage(product.thumbnailUrl)
                                    .setTitle(title)
                                    .setBody(body)
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .putData("click_action", product.getStoreUrl()) // 클릭 시 이동할 URL 추가
            .setToken(fcmToken.token)
            .build()

        try {
            firebaseMessaging.send(message)
        } catch (e: Exception) {
            log.error("Failed to send message: $message")
            log.error("Error: $e")
            return
        }

        log.info("Successfully sent message: $message")
    }
}
