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

        // 중복 제거
        val fcmList = basketAndMemberList.map { it.fcmToken }.distinct()
        val memberList = basketAndMemberList.map { it.member }.distinct()
        val basketList = basketAndMemberList.map { it.basket }.distinct()

        fcmList.forEach {
            fcmSendGateway.sendFcmMessage(fcmToken = it, product = product)
        }

        memberList.forEach {
            sendMailGateway.sendMail(it,  product)
        }

        basketList.forEach {
            addAlilmPort.addAlilm(Alilm.from(basket = it))
            it.sendAlilm()
            addBasketPort.addBasket(it, memberId = it.memberId, productId = product.id!!)
        }
    }
}