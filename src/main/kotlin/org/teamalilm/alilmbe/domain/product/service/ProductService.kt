package org.teamalilm.alilmbe.domain.product.service

import ProductFindAllData
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.controller.product.data.ProductSaveRequestData
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
    fun registration(productSaveRequestData: ProductSaveRequestData, member: Member) {
        // 유일 상품 정보
        val productInfo = ProductInfo(
            store = productSaveRequestData.store,
            number = productSaveRequestData.number,
            option1 = productSaveRequestData.option1,
            option2 = productSaveRequestData.option2,
            option3 = productSaveRequestData.option3
        )

        val product = productRepository.findByProductInfo(productInfo)
            ?: Product(
                name = productSaveRequestData.name,
                productInfo = ProductInfo(
                    store = productSaveRequestData.store,
                    number = productSaveRequestData.number,
                    option1 = productSaveRequestData.option1,
                    option2 = productSaveRequestData.option2,
                    option3 = productSaveRequestData.option3
                )
            ).also { productRepository.save(it) }

        if (basketRepository.existsByProductAndMember(product, member)) {
            throw RuntimeException("이미 장바구니에 담긴 상품입니다.")
        }

        basketRepository.save(Basket(product = product, member = member))
    }

    fun findAll(): List<ProductFindAllData> {
        val products = productRepository.findAllByOrderByCreatedDateDesc()

        return products.map { ProductFindAllData.of(it) }
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