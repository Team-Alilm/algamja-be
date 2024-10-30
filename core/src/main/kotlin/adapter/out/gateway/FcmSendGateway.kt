package org.team_alilm.adapter.out.gateway

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
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

        // FCM 메시지 구성 (Data 메시지로만)
        val message = Message.builder()
            .putData("ico", "https://file.notion.so/f/f/c345e317-1a77-4e86-8b67-b491a5db92b8/732799dc-6ad9-46f8-8864-22308c10cdb8/free-icon-bells-7124213.png?table=block&id=1037b278-57a0-8022-8a73-ea04c03ae27e&spaceId=c345e317-1a77-4e86-8b67-b491a5db92b8&expirationTimestamp=1730354400000&signature=hBdHPuerhscY6rXIkAe40sWyyvEq22eyqZ7AqA2Gt5o&downloadName=free-icon-bells-7124213.png")
            .putData("title", "[${product.name}] 상품이 재 입고 되었습니다!")
            .putData("body", """
                ${if (options.isNotBlank()) "option : $options" else ""}
                지금 바로 확인해보세요. 서버에서 발송하는 FCM입니다.
            """.trimIndent())
            .putData("image", product.imageUrl) // 이미지 URL 추가
            .putData("click_action", "https://www.musinsa.com/products/${product.number}")
            .setToken(fcmToken.token)
            .build()

        firebaseMessaging.send(message)

        log.info("Successfully sent message: $message")
    }
}
