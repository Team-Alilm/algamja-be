package org.team_alilm.adapter.out.gateway

import com.google.firebase.messaging.*
import domain.FcmToken
import domain.product.Product
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class FcmSendGateway(
    private val firebaseMessaging: FirebaseMessaging
) {

    private val log = LoggerFactory.getLogger(FcmSendGateway::class.java)

    fun sendFcmMessage(
        product: Product,
        fcmToken: FcmToken,
        platform: String // "ios", "android", "web"
    ) {
        val title = "[${product.name}] 상품이 재입고 되었습니다!"
        val options = listOfNotNull(product.firstOption, product.secondOption, product.thirdOption)
            .joinToString(" / ")
        val body = """
        ${if (options.isNotBlank()) "option : $options" else ""}
        지금 바로 확인해보세요.
    """.trimIndent()

        val messageBuilder = Message.builder()
            .putData("title", title)
            .putData("body", body)
            .putData("click_action", product.localServiceUrl()) // 클릭 시 이동할 URL
            .setToken(fcmToken.token)

        when (platform) {
            "ios" -> {
                messageBuilder.setApnsConfig(
                    ApnsConfig.builder()
                        .setAps(
                            Aps.builder()
                                .setCategory("product")
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
            }
            "android" -> {
                messageBuilder.setAndroidConfig(
                    AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        // Foreground에서는 `setNotification()` 없이 putData만 사용
                        .build()
                )
            }
            "web" -> {
                messageBuilder.setWebpushConfig(
                    WebpushConfig.builder()
                        .setNotification(
                            WebpushNotification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .setIcon(product.thumbnailUrl)
                                .build()
                        )
                        .build()
                )
                    .putData("click_action", product.localServiceUrl())
            }
        }

        val message = messageBuilder.build()

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
