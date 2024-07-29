package org.teamalilm.alilmbe.adapter.out.persistence.adapter

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component
import org.teamalilm.alilmbe.adapter.out.persistence.entity.member.MemberJpaEntity
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.ProductJpaEntity
import org.teamalilm.alilmbe.adapter.out.persistence.mapper.BasketMapper
import org.teamalilm.alilmbe.adapter.out.persistence.mapper.MemberMapper
import org.teamalilm.alilmbe.adapter.out.persistence.mapper.ProductMapper
import org.teamalilm.alilmbe.adapter.out.persistence.repository.basket.BasketRepository
import org.teamalilm.alilmbe.adapter.out.persistence.repository.basket.SpringDataBasketRepository
import org.teamalilm.alilmbe.application.port.out.Basket.AddBasketPort
import org.teamalilm.alilmbe.application.port.out.Basket.LoadBasketPort
import org.teamalilm.alilmbe.application.port.out.product.LoadBasketSlicePort
import org.teamalilm.alilmbe.domain.basket.Basket
import org.teamalilm.alilmbe.domain.member.Member
import org.teamalilm.alilmbe.domain.member.Member.*
import org.teamalilm.alilmbe.domain.product.Product
import org.teamalilm.alilmbe.domain.product.Product.*

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