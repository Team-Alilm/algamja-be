package org.teamalilm.alilmbe.service.alilm

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.adapter.out.persistence.jpa.entity.basket.Basket
import org.teamalilm.alilmbe.adapter.out.persistence.jpa.entity.member.Member
import org.teamalilm.alilmbe.adapter.out.persistence.jpa.repository.BasketRepository
import org.teamalilm.alilmbe.domain.product.entity.Product
import org.teamalilm.alilmbe.domain.product.entity.Product.ProductInfo
import org.teamalilm.alilmbe.domain.product.entity.Product.ProductInfo.Store
import org.teamalilm.alilmbe.domain.product.repository.ProductRepository
import org.teamalilm.alilmbe.global.slack.service.SlackService

@Service
@Transactional(readOnly = true)
class AlilmService(
    private val productRepository: ProductRepository,
    private val slackService: SlackService,
    private val basketRepository: BasketRepository
) {

    private val log = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registerProduct(command: AlilmRegistrationCommand) {
        val productInfo = ProductInfo(
            store = command.store,
            number = command.number,
            option1 = command.option1,
            option2 = command.option2,
            option3 = command.option3
        )

        val product = findOrCreateProduct(command, productInfo)

        if (basketRepository.existsByMemberAndProduct(command.member, product)) {
            log.info("Product already registered: ${product.name}")
        } else {
            log.info("Registering product: ${product.name}")
            basketRepository.save(Basket(member = command.member, product = product))
        }

        notifySlack(command.member, product)
    }

    private fun findOrCreateProduct(command: AlilmRegistrationCommand, productInfo: ProductInfo): Product {
        return productRepository.findByProductInfo(productInfo)
            ?: productRepository.save(
                Product(
                    name = command.name,
                    brand = command.brand,
                    imageUrl = command.imageUrl,
                    category = command.category,
                    price = command.price,
                    productInfo = productInfo
                )
            ).also { log.info("Created new product: $it") }
    }

    private fun notifySlack(member: Member, product: Product) {
        slackService.sendSlackMessage(
            """
            id: ${member.id}
            nickname: ${member.nickname} has registered a new product.
            Product: $product
            """.trimIndent()
        )
    }

    data class AlilmRegistrationCommand(
        val number: Int,
        val name: String,
        val brand: String,
        val store: Store,
        val imageUrl: String,
        val category: String,
        val price: Int,
        val option1: String,
        val option2: String?,
        val option3: String?,
        val member: Member
    )
}
