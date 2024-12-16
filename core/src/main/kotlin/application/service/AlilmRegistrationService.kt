package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.adapter.out.gateway.SlackGateway
import org.team_alilm.application.port.`in`.use_case.AlilmRegistrationUseCase.*
import org.team_alilm.domain.Basket
import org.team_alilm.domain.Member.*
import org.team_alilm.domain.product.Product
import org.team_alilm.domain.product.ProductId
import org.team_alilm.domain.product.ProductImage
import org.team_alilm.global.error.BasketAlreadyExistsException
import org.team_alilm.global.util.StringConstant

@Service
@Transactional(readOnly = true)
class AlilmRegistrationService(
    private val loadProductPort: org.team_alilm.application.port.out.LoadProductPort,
    private val addProductPort: org.team_alilm.application.port.out.AddProductPort,
    private val loadBasketPort: org.team_alilm.application.port.out.LoadBasketPort,
    private val addBasketPort: org.team_alilm.application.port.out.AddBasketPort,
    private val slackGateway: SlackGateway,
    private val loadProductImagePort: org.team_alilm.application.port.out.LoadProductImagePort,
    private val addAllProductImagePort: org.team_alilm.application.port.out.AddAllProductImagePort
) : org.team_alilm.application.port.`in`.use_case.AlilmRegistrationUseCase {

    @Transactional
    override fun alilmRegistration(command: AlilmRegistrationCommand) {
        val product = createAndSaveProductWithImages(command)
        saveBasket(
            memberId = command.member.id!!,
            productId = product.id!!
        )

        slackGateway.sendMessage(
            message = """
                |알림 등록 완료
                |회원: ${command.member.nickname}
                |상품명: ${product.name}
                |링크: ${StringConstant.MUSINSA_PRODUCT_HTML_URL.get().format(product.number)}
            """.trimIndent()
        )
    }

    private fun saveBasket(
        memberId: MemberId,
        productId: ProductId,
    ) {

        val basket = loadBasketPort.loadBasketIncludeIsDelete(
            memberId = memberId,
            productId = productId
        ) ?.let {
            if(it.isReRegisterable().not()) throw BasketAlreadyExistsException()

            it
        } ?: run {
            Basket(
                id = Basket.BasketId(null),
                memberId = memberId,
                productId = productId,
                isHidden = false,
            )
        }

        addBasketPort.addBasket(
            basket = basket,
            memberId = memberId,
            productId = productId
        )
    }

    private fun createAndSaveProductWithImages(command: AlilmRegistrationCommand) : Product =
        loadProductPort.loadProduct(
            number = command.number,
            store = command.store,
            firstOption = command.firstOption,
            secondOption = command.secondOption,
            thirdOption = command.thirdOption
        ) ?: run {
            val product = addProductPort.addProduct(
                Product(
                    id = null,
                    number = command.number,
                    name = command.name,
                    brand = command.brand,
                    store = command.store,
                    thumbnailUrl = command.thumbnailUrl,
                    firstCategory = command.firstCategory,
                    secondCategory = command.secondCategory,
                    price = command.price,
                    firstOption = command.firstOption,
                    secondOption = command.secondOption,
                    thirdOption = command.thirdOption
                )
            )

            loadProductImagePort.existsByProductImage(product.number, product.store)
                .takeIf { it.not() } // 이미지가 존재하지 않으면
                ?.let {
                    saveProductImageList(product, command.imageUrlList) // 이미지를 저장합니다
                }

            return product
        }

    private fun saveProductImageList(product: Product, imageUrlList: List<String>) {
        addAllProductImagePort.addAllProductImage(
            productImageList = imageUrlList.map {
                ProductImage(
                    id = null,
                    imageUrl = it,
                    productNumber = product.number,
                    productStore = product.store
                )
            }
        )
    }
}
