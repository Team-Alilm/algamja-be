package org.team_alilm.application.service

import domain.product.Product
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.team_alilm.application.port.out.gateway.SendMailGateway
import org.team_alilm.application.port.out.gateway.SendSlackGateway
import org.team_alilm.adapter.out.gateway.FcmSendGateway
import org.team_alilm.application.port.out.LoadBasketAndMemberPort

@Service
class NotificationService(
//    private val sendSlackGateway: SendSlackGateway,
//    private val sendMailGateway: SendMailGateway,
//    private val fcmSendGateway: FcmSendGateway,
//    private val addBasketPort: AddBasketPort,
    private val loadBasketAndMemberPort: LoadBasketAndMemberPort,
//    private val addAlilmPort: AddAlilmPort,
//    private val restClient: RestClient
) {

    fun sendNotifications(product: Product) {
        val basketAndMemberList = loadBasketAndMemberPort.loadBasketAndMember(product)

        basketAndMemberList.forEach { (basket, member, fcmToken) ->
//            fcmSendGateway.sendFcmMessage(member = member, fcmToken = fcmToken, product = product)
//            sendSlackGateway.sendMessage(product)
//            sendMailGateway.sendMail(member.email, member.nickname, product)
//            restClient.put()
//                .uri("https://alilm.store")
//            addAlilmPort.addAlilm(Alilm.from(basket = basket))
//            basket.sendAlilm()
//            addBasketPort.addBasket(basket, memberId = member.id!!, productId = product.id!!)
        }
    }
}