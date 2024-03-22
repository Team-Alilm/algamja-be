package org.teamalilm.alilmbe.domain.product.service

import ProductFindAllData
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.controller.product.data.ProductSaveRequestBody
import org.teamalilm.alilmbe.domain.basket.entity.Basket
import org.teamalilm.alilmbe.domain.basket.repository.BasketRepository
import org.teamalilm.alilmbe.domain.member.entity.Member
import org.teamalilm.alilmbe.domain.product.entity.Product
import org.teamalilm.alilmbe.domain.product.entity.ProductInfo
import org.teamalilm.alilmbe.domain.product.error.exception.DuplicateProductInBasketException
import org.teamalilm.alilmbe.domain.product.repository.ProductRepository

/**
 *  ProductService
 *
 *  @author SkyLabs
 *  @version 1.0.0
 *  @date 2024-03-22
 **/
@Service
@Transactional(readOnly = false)
class ProductService(
    private val productRepository: ProductRepository,
    private val basketRepository: BasketRepository
) {

    /**
     * 상품을 등록해요.
     */
    @Transactional
    fun registration(productSaveRequestBody: ProductSaveRequestBody, member: Member) {
        // 유일 상품 정보
        val productInfo = ProductInfo(
            store = productSaveRequestBody.store,
            number = productSaveRequestBody.number,
            option1 = productSaveRequestBody.option1,
            option2 = productSaveRequestBody.option2,
            option3 = productSaveRequestBody.option3
        )

        val product = productRepository.findByProductInfo(productInfo)
            ?: Product(
                name = productSaveRequestBody.name,
                imageUrl = productSaveRequestBody.imageUrl,
                productInfo = ProductInfo(
                    store = productSaveRequestBody.store,
                    number = productSaveRequestBody.number,
                    option1 = productSaveRequestBody.option1,
                    option2 = productSaveRequestBody.option2,
                    option3 = productSaveRequestBody.option3
                )
            ).also { productRepository.save(it) }

        if (basketRepository.existsByProductAndMember(product, member)) {
            throw DuplicateProductInBasketException()
        }

        basketRepository.save(
            Basket(
                product = product,
                member = member
            )
        )
    }

    fun findAll(): List<ProductFindAllData> {
        val products = productRepository.findAllByOrderByCreatedDateDesc()

        return products.map { ProductFindAllData.of(it) }
    }

}