package org.teamalilm.alilmbe.adapter.out.persistence.adapter

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component
import org.teamalilm.alilmbe.adapter.out.persistence.entity.MemberJpaEntity
import org.teamalilm.alilmbe.adapter.out.persistence.entity.ProductJpaEntity
import org.teamalilm.alilmbe.adapter.out.persistence.mapper.BasketMapper
import org.teamalilm.alilmbe.adapter.out.persistence.mapper.ProductMapper
import org.teamalilm.alilmbe.adapter.out.persistence.repository.BasketRepository
import org.teamalilm.alilmbe.adapter.out.persistence.repository.spring_data.SpringDataBasketRepository
import org.teamalilm.alilmbe.application.port.out.AddBasketPort
import org.teamalilm.alilmbe.application.port.out.LoadBasketPort
import org.teamalilm.alilmbe.application.port.out.LoadBasketSlicePort
import org.teamalilm.alilmbe.domain.Basket
import org.teamalilm.alilmbe.domain.Member.*
import org.teamalilm.alilmbe.domain.Product
import org.teamalilm.alilmbe.domain.Product.*

@Component
class BasketPersistenceAdapter(
    private val springDataBasketRepository: SpringDataBasketRepository,
    private val basketRepository: BasketRepository,
    private val basketMapper: BasketMapper,
    private val productMapper: ProductMapper
) : AddBasketPort, LoadBasketPort, LoadBasketSlicePort {

    override fun addBasket(
        basket: Basket,
        memberJpaEntity: MemberJpaEntity,
        product: Product
    ): Basket {
        val basketJpaEntity = springDataBasketRepository.save(
                basketMapper.mapToJpaEntity(
                basket,
                    memberJpaEntity,
                productMapper.mapToJpaEntity(product)
            )
        )

        return basketMapper.mapToDomainEntity(basketJpaEntity)
    }

    override fun loadBasket(
        memberId: MemberId,
        productId: ProductId
    ): Basket? {
        val basketJpaEntity = springDataBasketRepository.findByMemberJpaEntityIdAndProductJpaEntityId(
            memberJpaEntityId = memberId.value,
            productJpaEntityId = productId.value
        )

        return basketMapper.mapToDomainEntityOrNull(basketJpaEntity)
    }

    override fun loadBasketSlice(pageRequest: PageRequest): Slice<BasketCountData> {
        val basketCountProjectionSlice = basketRepository.loadBasketSlice(pageRequest)

        return basketCountProjectionSlice.map {
            val productJpaEntity = it.get("productJpaEntity", ProductJpaEntity::class.java)!!
            val waitingCount = it.get("waitingCount", Long::class.java)!!

            BasketCountData(
                product = productMapper.mapToDomainEntity(productJpaEntity),
                waitingCount = waitingCount
            )
        }
    }

    data class BasketCountData(
        val product: Product,
        val waitingCount: Long
    )
}