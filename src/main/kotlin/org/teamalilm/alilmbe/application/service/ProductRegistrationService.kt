package org.teamalilm.alilmbe.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.adapter.out.persistence.repository.SpringDataProductRepository
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductRegistrationCommand
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductRegistrationUseCase

@Service
@Transactional(readOnly = true)
class ProductRegistrationService(
    private val springDataProductRepository: SpringDataProductRepository
) : ProductRegistrationUseCase {

    private val log = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun invoke(command: ProductRegistrationCommand) {


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
