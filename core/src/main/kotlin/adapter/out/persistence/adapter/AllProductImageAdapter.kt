package org.team_alilm.adapter.out.persistence.adapter

import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.mapper.ProductImageMapper
import org.team_alilm.adapter.out.persistence.repository.spring_data.SpringDataProductImageRepository
import org.team_alilm.application.port.out.AddAllProductImagePort
import org.team_alilm.domain.product.ProductImage

@Component
class AllProductImageAdapter(
    private val productImageMapper: ProductImageMapper,
    private val springDataProductImagePort: SpringDataProductImageRepository
) : AddAllProductImagePort {

    override fun addAllProductImage(productImageList: List<ProductImage>) {
        productImageMapper.mapToDomainList(
            springDataProductImagePort.saveAll(
                productImageMapper.mapToJpaEntityList(productImageList)
            )
        )
    }
}