package org.teamalilm.alilmbe.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.adapter.out.persistence.repository.product.SpringDataProductRepository
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductRegistrationCommand
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductRegistrationUseCase
import org.teamalilm.alilmbe.application.port.out.AddProductPort
import org.teamalilm.alilmbe.domain.product.Product

@Service
@Transactional(readOnly = true)
class ProductRegistrationService(
    private val addProductPort: AddProductPort
) : ProductRegistrationUseCase {

    private val log = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun invoke(command: ProductRegistrationCommand) {
        val product = Product(
            id = Product.ProductId(null),
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
        log.info("product: $product")

        addProductPort.addProduct(product)

//        log.info("Product registered: ${product.name}")
    }

//    @Transactional
//    override fun invoke(command: AlilmRegistrationCommand) {
//        val productInfo = ProductInfo(
//            store = command.store,
//            number = command.number,
//            option1 = command.option1,
//            option2 = command.option2,
//            option3 = command.option3
//        )
//
//        val product = findOrCreateProduct(command, productInfo)
//
//        if (basketRepository.existsByMemberAndProduct(command.member, product)) {
//            log.info("Product already registered: ${product.name}")
//        } else {
//            log.info("Registering product: ${product.name}")
//            basketRepository.save(Basket(member = command.member, product = product))
//        }
//
//        notifySlack(command.member, product)
//    }

}
