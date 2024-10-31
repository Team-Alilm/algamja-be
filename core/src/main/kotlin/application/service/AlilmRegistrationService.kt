package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.AlilmRegistrationUseCase.*
import org.team_alilm.domain.Basket
import org.team_alilm.domain.Product
import org.team_alilm.global.error.BasketAlreadyExistsException

@Service
@Transactional(readOnly = true)
class AlilmRegistrationService(
    private val loadProductPort: org.team_alilm.application.port.out.LoadProductPort,
    private val addProductPort: org.team_alilm.application.port.out.AddProductPort,
    private val loadBasketPort: org.team_alilm.application.port.out.LoadBasketPort,
    private val addBasketPort: org.team_alilm.application.port.out.AddBasketPort
) : org.team_alilm.application.port.`in`.use_case.AlilmRegistrationUseCase {

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

        val basket = loadBasketPort.loadBasketIncludeIsDelete(
            memberId = command.member.id!!,
            productId = product.id!!
        ) ?.let {
            /*
                알림을 이미 받은 상품
                삭제한 상품은
                재 등록 시 복구 합니다.
            */
            if(it.isAlilm || it.isDelete) {
                it.isAlilm = false
                it.alilmDate = null
                it.isDelete = false
            } else {
                // 알림을 받지도 삭제 하지도 않았다면 기다리는 상품이 있는 것 입니다.
                log.info("장바구니가 이미 존재합니다. memberId: ${command.member.id}, productId: ${product.id}")
                throw BasketAlreadyExistsException()
            }

            it
        } ?: run {
            log.info("장바구니를 등록 합니다.")
            Basket(
                id = Basket.BasketId(null),
                memberId = command.member.id,
                productId = product.id,
                isHidden = false,
            )
        }

        addBasketPort.addBasket(
            basket = basket,
            member = command.member,
            product = product
        )
    }

    private fun getProduct(command: AlilmRegistrationCommand) =
        loadProductPort.loadProduct(
            number = command.number,
            store = command.store,
            firstOption = command.firstOption,
            secondOption = command.secondOption,
            thirdOption = command.thirdOption
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
                    firstOption = command.firstOption,
                    secondOption = command.secondOption,
                    thirdOption = command.thirdOption
                )
            )
        }

}
