package org.team_alilm.adapter.out.persistence.adapter

import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.entity.PriceJpaEntity
import org.team_alilm.adapter.out.persistence.mapper.PriceMapper
import org.team_alilm.adapter.out.persistence.mapper.ProductMapper
import org.team_alilm.adapter.out.persistence.repository.PriceRepository
import org.team_alilm.adapter.out.persistence.repository.spring_data.SpringDataPriceRepository
import org.team_alilm.application.port.out.AddPricePort
import org.team_alilm.application.port.out.LoadPricePort
import org.team_alilm.domain.Price
import org.team_alilm.domain.Product
import org.team_alilm.global.util.DateFormatter.dateFormatter


@Component
class PriceAdapter(
    private val springDataPriceRepository: SpringDataPriceRepository,
    private val priceRepository: PriceRepository,
    private val priceMapper: PriceMapper,
    private val productMapper: ProductMapper
) :
    AddPricePort,
    LoadPricePort {

    override fun addPrice(
        price: Int,
        product: Product
    ): Price {
        val priceJpaEntity = priceJpaEntity(price, product)
        springDataPriceRepository.save(priceJpaEntity)

        return priceMapper.mapToDomainEntity(priceJpaEntity)
    }

    override fun loadPrice (
        productId: Product.ProductId
    ): List<LoadPricePort.PriceHistory>? {
        return springDataPriceRepository.findAllByProductJpaEntityIdAndIsDeleteFalseOrderByCreatedDateDesc(
            productId.value
        )?.let {
            it.map {
                LoadPricePort.PriceHistory(
                    price = it.price,
                    date = dateFormatter(it.createdDate)
                )
            }
        }
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