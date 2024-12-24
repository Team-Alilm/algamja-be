package org.team_alilm.application.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.out.AddBasketPort
import org.team_alilm.application.port.out.gateway.SendMailGateway
import org.team_alilm.application.port.out.gateway.SendSlackGateway
import org.team_alilm.adapter.out.gateway.FcmSendGateway
import org.team_alilm.application.port.out.AddAlilmPort
import org.team_alilm.application.port.out.LoadBasketAndMemberPort
import org.team_alilm.domain.Alilm
import org.team_alilm.domain.product.Product

@Service
@Transactional
class NotificationService(
    private val sendSlackGateway: SendSlackGateway,
    private val sendMailGateway: SendMailGateway,
    private val fcmSendGateway: FcmSendGateway,
    private val addBasketPort: AddBasketPort,
    private val loadBasketAndMemberPort: LoadBasketAndMemberPort,
    private val addAlilmPort: AddAlilmPort
) {

    private val log = LoggerFactory.getLogger(NotificationService::class.java)

    fun sendNotifications(product: Product) {
        val basketAndMemberList = loadBasketAndMemberPort.loadBasketAndMember(product)

        basketAndMemberList.forEach() { (member) ->
            log.info("member : $member")
        }

        basketAndMemberList.forEach { (basket, member, fcmToken) ->
            fcmSendGateway.sendFcmMessage(member = member, fcmToken = fcmToken, product = product)
        }

        basketAndMemberList.isNotEmpty().let {
            val (basket, member) = basketAndMemberList.first()
            sendSlackGateway.sendMessage(product)
            addBasketPort.addBasket(basket, memberId = member.id!!, productId = product.id!!)
            sendMailGateway.sendMail(member.email, member.nickname, product)
            basket.sendAlilm()
            addAlilmPort.addAlilm(Alilm.from(basket = basket) )
        }
    }

    fun notifySlackError(product: Product, errorMessage: String?) {
        val message = "서버에 요청 시 에러가 발생했어요.: ${product.number}\nError: $errorMessage"
        sendSlackGateway.sendMessage(message)
    }
}