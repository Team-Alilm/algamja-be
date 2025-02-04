package org.team_alilm.adapter.out.gateway

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
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
        // 옵션들 중 null이 아닌 값을 필터링하여 메시지에 포함
        val options = listOfNotNull(product.firstOption, product.secondOption, product.thirdOption)
            .joinToString(" / ")

        // FCM 메시지 구성 (Data 메시지로만)
        val message = Message.builder()
            .putData("ico", "https://file.notion.so/f/f/c345e317-1a77-4e86-8b67-b491a5db92b8/732799dc-6ad9-46f8-8864-22308c10cdb8/free-icon-bells-7124213.png?table=block&id=1037b278-57a0-8022-8a73-ea04c03ae27e&spaceId=c345e317-1a77-4e86-8b67-b491a5db92b8&expirationTimestamp=1730354400000&signature=hBdHPuerhscY6rXIkAe40sWyyvEq22eyqZ7AqA2Gt5o&downloadName=free-icon-bells-7124213.png")
            .putData("title", "[${product.name}] 상품이 재 입고 되었습니다!")
            .putData("body", """
                ${if (options.isNotBlank()) "option : $options" else ""}
                지금 바로 확인해보세요.
            """.trimIndent())
            .putData("image", product.thumbnailUrl) // 이미지 URL 추가
            .putData("click_action", product.getStoreUrl()) // 클릭 시 이동할 URL 추가
            .setToken(fcmToken.token)
            .build()

        try {
            firebaseMessaging.send(message)
        } catch (e: Exception) {
            log.error("Failed to send message: $message", e)
            return
        }

        log.info("Successfully sent message: $message")
    }
}
