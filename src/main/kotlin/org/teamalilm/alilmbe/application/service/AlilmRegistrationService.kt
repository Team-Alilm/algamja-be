package org.teamalilm.alilmbe.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.application.port.`in`.use_case.AlilmRegistrationUseCase
import org.teamalilm.alilmbe.application.port.`in`.use_case.AlilmRegistrationUseCase.*
import org.teamalilm.alilmbe.application.port.out.Basket.AddBasketPort
import org.teamalilm.alilmbe.application.port.out.Basket.LoadBasketPort
import org.teamalilm.alilmbe.application.port.out.product.AddProductPort
import org.teamalilm.alilmbe.application.port.out.product.LoadProductPort
import org.teamalilm.alilmbe.common.error.BasketAlreadyExistsException
import org.teamalilm.alilmbe.common.error.ErrorMessage
import org.teamalilm.alilmbe.domain.basket.Basket
import org.teamalilm.alilmbe.domain.member.Member
import org.teamalilm.alilmbe.domain.member.Member.*
import org.teamalilm.alilmbe.domain.product.Product

@Service
@Transactional(readOnly = true)
class AlilmRegistrationService(
    private val loadProductPort: LoadProductPort,
    private val addProductPort: AddProductPort,
    private val loadBasketPort: LoadBasketPort,
    private val addBasketPort: AddBasketPort
) : AlilmRegistrationUseCase {

    private val log = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun alilmRegistration(command: AlilmRegistrationCommand) {
        val product = getProduct(command)
        saveBasket(command, product)
    }

    private fun saveBasket(
        command: AlilmRegistrationCommand,
        product: Product,
    ) {
        loadBasketPort.loadBasket(
            memberId = MemberId(command.memberJpaEntity.id!!),
            productId = product.id!!
        ) ?.let {
            log.info("장바구니가 이미 존재합니다. memberId: ${command.memberJpaEntity.id}, productId: ${product.id}")
            throw BasketAlreadyExistsException(ErrorMessage.BASKET_ALREADY_EXISTS)
        } ?: run {
            log.info("장바구니를 등록 합니다.")
            addBasketPort.addBasket(
                basket = Basket(
                    id = Basket.BasketId(null),
                    memberId = MemberId(command.memberJpaEntity.id!!),
                    productId = product.id!!
                ),
                memberJpaEntity = command.memberJpaEntity,
                product = product
            )
        }
    }

    private fun getProduct(command: AlilmRegistrationCommand) =
        loadProductPort.loadProduct(
            number = command.number,
            store = command.store,
            option1 = command.option1,
            option2 = command.option2,
            option3 = command.option3
        ) ?: run {
            addProductPort.addProduct(
                Product(
                    id = null,
                    number = command.number,
                    name = command.name,
                    brand = command.brand,
                    store = command.store,
                    imageUrl = command.imageUrl,
                    category = command.category,
                    price = command.price,
                    option1 = command.option1,
                    option2 = command.option2,
                    option3 = command.option3
                )
            )
        }

}
