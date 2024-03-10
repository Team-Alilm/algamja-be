package org.teamalilm.alilmbe.domain.product.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.controller.ProductFindAllView
import org.teamalilm.alilmbe.controller.ProductSaveForm
import org.teamalilm.alilmbe.domain.product.entity.Product
import org.teamalilm.alilmbe.domain.product.entity.ProductInfo
import org.teamalilm.alilmbe.domain.product.repository.ProductRepository

@Service
@Transactional(readOnly = false)
class ProductService(
    val productRepository: ProductRepository,
) {

    @Transactional
    fun registration(productSaveForm: ProductSaveForm) {
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

        val product = Product(
            name = productSaveForm.name,
            imageUrl = productSaveForm.imageUrl,
            productInfo = ProductInfo(
                number = productSaveForm.productNumber,
                option1 = productSaveForm.option1,
                option2 = productSaveForm.option2
            ),
            store = productSaveForm.store
        )

        productRepository.save(product)
    }

    fun findAll(): List<ProductFindAllView> {
        val products = productRepository.findAllByOrderByCreatedDateDesc()

        return products.map { ProductFindAllView.of(it) }
    }

}