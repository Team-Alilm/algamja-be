package org.team_alilm.application.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.team_alilm.application.port.out.AddBasketPort
import org.team_alilm.application.port.out.gateway.SendMailGateway
import org.team_alilm.application.port.out.gateway.SendSlackGateway
import org.team_alilm.adapter.out.gateway.FcmSendGateway
import org.team_alilm.application.port.out.LoadBasketAndMemberPort
import org.team_alilm.domain.product.Product

@Service
class NotificationService(
    private val sendSlackGateway: SendSlackGateway,
    private val sendMailGateway: SendMailGateway,
    private val fcmSendGateway: FcmSendGateway,
    private val addBasketPort: AddBasketPort,
    private val loadBasketAndMemberPort: LoadBasketAndMemberPort,
) {
    private val log = LoggerFactory.getLogger(NotificationService::class.java)

    fun sendNotifications(product: Product) {
        val basketAndMemberList = loadBasketAndMemberPort.loadBasketAndMember(product)

        basketAndMemberList.forEach() { (member) ->
            log.info("member : $member")
        }

        basketAndMemberList.forEach { (basket, member, fcmToken) ->
            basket.sendAlilm()
            addBasketPort.addBasket(basket, memberId = member.id!!, productId = product.id!!)

            sendSlackGateway.sendMessage(product)
            sendMailGateway.sendMail(member.email, member.nickname, product)
            fcmSendGateway.sendFcmMessage(member = member, fcmToken = fcmToken, product = product)
        }
    }

    fun notifySlackError(product: Product, errorMessage: String?) {
        val message = "서버에 요청 시 에러가 발생했어요.: ${product.number}\nError: $errorMessage"
        sendSlackGateway.sendMessage(message)
    }
}