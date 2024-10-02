package org.teamalilm.alilm.adapter.out.gateway

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.teamalilm.alilm.domain.FcmToken
import org.teamalilm.alilm.domain.Member
import org.teamalilm.alilm.domain.Product
import org.teamalilm.alilm.global.security.service.oAuth2.data.Provider

@SpringBootTest
class FcmSendGatewayTest {

    @Autowired
    private lateinit var fcmSendGateway: FcmSendGateway

    @Test
    fun sendFcmMessage() {
        var member = Member(
            provider = Provider.KAKAO,
            providerId = 1L,
            email = "c",
            nickname = "c",
        )

        var product = Product(
            id = Product.ProductId(1L),
            number = 1L,
            name = "c",
            brand = "c",
            imageUrl = "c",
            category = "c",
            price = 1,
            store = Product.Store.MUSINSA,
            firstOption = "c",
            secondOption = "c",
            thirdOption = "c"
        )

        val fcmToken = FcmToken(
            token = "fm3LCU4VwnHXsbQpCC9_f4:APA91bGEDgfJTgx8PCc1ZKpEBPvkcz9kPVSfOpI9c3X7ZLIPa5ujaf8eMoU0jX3s3EnMI2IEVWHWJkQAhtIqzhaQzlS1JgW5wbftNwvIzBw12JuSkPtwF0PIVxm-_8oaFPgoFR4t19fc",
            memberId = Member.MemberId(1L)
        )

        val response = fcmSendGateway.sendFcmMessage(member, product ,fcmToken)

        println("FCM 메시지 전송 결과: $response")
    }
}
