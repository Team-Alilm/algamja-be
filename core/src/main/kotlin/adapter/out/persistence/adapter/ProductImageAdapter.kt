package org.team_alilm.adapter.out.persistence.adapter

import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.repository.spring_data.SpringDataProductImageRepository
import org.team_alilm.application.port.out.LoadProductImagePort
import org.team_alilm.domain.product.Store

@Component
class ProductImageAdapter(
    private val springDataProductImageRepository: SpringDataProductImageRepository
) : LoadProductImagePort {

    override fun existsByProductImage(productNumber: Long, productStore: Store): Boolean {
        return springDataProductImageRepository.existsByProductNumberAndProductStore(productNumber, productStore)
    }
}