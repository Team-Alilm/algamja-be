package org.teamalilm.alilm.adapter.out.persistence.adapter

import org.springframework.stereotype.Component
import org.teamalilm.alilm.adapter.out.persistence.entity.PriceJpaEntity
import org.teamalilm.alilm.adapter.out.persistence.mapper.PriceMapper
import org.teamalilm.alilm.adapter.out.persistence.mapper.ProductMapper
import org.teamalilm.alilm.adapter.out.persistence.repository.PriceRepository
import org.teamalilm.alilm.adapter.out.persistence.repository.spring_data.SpringDataPriceRepository
import org.teamalilm.alilm.application.port.out.AddPricePort
import org.teamalilm.alilm.domain.Price
import org.teamalilm.alilm.domain.Product


@Component
class PriceAdapter(
    private val springDataPriceRepository: SpringDataPriceRepository,
    private val priceRepository: PriceRepository,
    private val priceMapper: PriceMapper,
    private val productMapper: ProductMapper
) :
    AddPricePort {

        private val log = { msg: String -> println(msg) }

    override fun addPrice(
        price: Int,
        product: Product
    ): Price {
        val priceJpaEntity = priceJpaEntity(price, product)
        springDataPriceRepository.save(priceJpaEntity)

        return priceMapper.mapToDomainEntity(priceJpaEntity)
    }

    private fun priceJpaEntity(
        price: Int,
        product: Product
    ): PriceJpaEntity {
        val priceJpaEntity = priceMapper
            .mapToJpaEntity(
                price,
                productMapper.mapToJpaEntity(product)
            )

        return priceJpaEntity
    }

}