package org.teamalilm.alilm.adapter.out.gateway

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.teamalilm.alilm.domain.Member
import org.teamalilm.alilm.domain.Product

@Service
class FcmSendGateway(
    private val firebaseMessaging: FirebaseMessaging
) {

    private val log = LoggerFactory.getLogger(FcmSendGateway::class.java)

    fun sendFcmMessage(
        member: Member,
        product: Product
    ) {
        // send fcm message
        val message = firebaseMessaging.send(Message.builder()
            .setNotification(
                Notification.builder()
                    .setTitle("Alilm")
                    .setBody("""
                        상품 명 : ${product.name} 
                        option1 : ${product.firstOption}
                        option2 : ${product.secondOption}
                        option3 : ${product.thirdOption}

                        상품이 재고가 있습니다.
                        바로 구매하러 가시겠습니까?
                        링크 : ${"https://www.musinsa.com/products/${product.number}"}
                    """.trimIndent())
                    .setImage("https://file.notion.so/f/f/c345e317-1a77-4e86-8b67-b491a5db92b8/732799dc-6ad9-46f8-8864-22308c10cdb8/free-icon-bells-7124213.png?table=block&id=1037b278-57a0-8022-8a73-ea04c03ae27e&spaceId=c345e317-1a77-4e86-8b67-b491a5db92b8&expirationTimestamp=1727503200000&signature=0rc_Pnx_oo5sGx7AD6GGMtFvxlhbv2yrh4VxDoVke_o&downloadName=free-icon-bells-7124213.png")
                    .build()
                ).setToken(member.fcmToken)
            .build()
        )

        log.info("Successfully sent message: $message")
    }

}