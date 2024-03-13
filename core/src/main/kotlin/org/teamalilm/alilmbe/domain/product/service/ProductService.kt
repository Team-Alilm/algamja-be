package org.teamalilm.alilmbe.domain.product.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.controller.ProductFindAllView
import org.teamalilm.alilmbe.controller.ProductSaveForm
import org.teamalilm.alilmbe.domain.basket.entity.Basket
import org.teamalilm.alilmbe.domain.basket.repository.BasketRepository
import org.teamalilm.alilmbe.domain.member.entity.Member
import org.teamalilm.alilmbe.domain.product.entity.Product
import org.teamalilm.alilmbe.domain.product.entity.ProductInfo
import org.teamalilm.alilmbe.domain.product.repository.ProductRepository

@Service
@Transactional(readOnly = false)
class ProductService(
    val productRepository: ProductRepository,
    val basketRepository: BasketRepository
) {

    /**
     * 상품을 등록해요.
     */
    @Transactional
    fun registration(productSaveForm: ProductSaveForm, member: Member) {
        // 유일 상품 정보
        val productInfo = ProductInfo(
            store = productSaveForm.store,
            number = productSaveForm.number,
            option1 = productSaveForm.option1,
            option2 = productSaveForm.option2,
            option3 = productSaveForm.option3
        )

        val product = productRepository.findByProductInfo(productInfo)
            ?: Product(
                name = productSaveForm.name,
                productInfo = ProductInfo(
                    number = productSaveForm.number,
                    store = productSaveForm.store,
                    option1 = productSaveForm.option1,
                    option2 = productSaveForm.option2,
                    option3 = productSaveForm.option3
                )
            ).also { productRepository.save(it) }

        if (basketRepository.existsByProductAndMember(product, member)) {
            throw RuntimeException("이미 장바구니에 담긴 상품입니다.")
        }

        basketRepository.save(Basket(product = product, member = member))
    }

    fun findAll(): List<ProductFindAllView> {
        val products = productRepository.findAllByOrderByCreatedDateDesc()

        return products.map { ProductFindAllView.of(it) }
    }

}

//        val productNumber = productSaveForm.productNumber
//        val url = "https://goods-detail.musinsa.com/goods/$productNumber/options?goodsSaleType=SALE"
//
//        try {
//            val response = RestClient
//                .create()
//                .get()
//                .uri(url)
//                .retrieve()
//                .body(Map::class.java) ?: throw RuntimeException("상품 데이터를 응답 받지 못했어요.")
//
//            val basicItems = (response["data"] as Map<*, *>)["basic"] as List<Map<String, Any>>
//            basicItems.forEach {
//                val name = it["name"] as String
//                val price = it["price"] as Int
//                val isSoldOut = it["isSoldOut"] as Boolean
//                val remainQuantity = it["remainQuantity"] as Int