package org.team_alilm.application.service

import domain.Alilm
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.adapter.out.gateway.FcmSendGateway
import org.team_alilm.application.port.`in`.use_case.BasketAlilmUseCase
import org.team_alilm.application.port.out.AddAlilmPort
import org.team_alilm.application.port.out.AddBasketPort
import org.team_alilm.application.port.out.LoadBasketAndMemberPort
import org.team_alilm.application.port.out.LoadProductPort
import org.team_alilm.application.port.out.gateway.SendMailGateway
import org.team_alilm.global.error.NotFoundProductException

@Service
@Transactional
class BasketAlilmService(
    private val sendMailGateway: SendMailGateway,
    private val fcmSendGateway: FcmSendGateway,
    private val addBasketPort: AddBasketPort,
    private val loadProductPort: LoadProductPort,
    private val loadBasketAndMemberPort: LoadBasketAndMemberPort,
    private val addAlilmPort: AddAlilmPort,
): BasketAlilmUseCase {

    @Transactional
    override fun basketAlilm(command: BasketAlilmUseCase.BasketAlilmCommand) {
        val product = loadProductPort.loadProduct(command.productId) ?: throw NotFoundProductException()
        val basketAndMemberList = loadBasketAndMemberPort.loadBasketAndMember(product)

        basketAndMemberList.forEach { (basket, member, fcmToken) ->
             fcmSendGateway.sendFcmMessage(member = member, fcmToken = fcmToken, product = product)
             sendMailGateway.sendMail(member.email, member.nickname, product)
             addAlilmPort.addAlilm(Alilm.from(basket = basket))
             basket.sendAlilm()
             addBasketPort.addBasket(basket, memberId = member.id!!, productId = product.id!!)
        }
    }
}