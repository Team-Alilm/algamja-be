package org.teamalilm.alilmbe.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.ProductJpaEntity
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductCrawlingCommand
import org.teamalilm.alilmbe.domain.product.Product

@Component
class ProductMapper {

    fun mapToJpaEntity(product: Product): ProductJpaEntity {
        return ProductJpaEntity(
            id = product.id.value,
            number = product.number,
            name = product.name,
            brand = product.brand,
            imageUrl = product.imageUrl,
            store = product.store,
            category = product.category,
            price = product.price,
            waitingCount = product.waitingCount,
            option1 = product.option1,
            option2 = product.option2,
            option3 = product.option3
        )
    }

}