package org.teamalilm.alilmbe.adapter.out.persistence.adapter

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component
import org.teamalilm.alilmbe.adapter.out.persistence.mapper.BasketMapper
import org.teamalilm.alilmbe.adapter.out.persistence.mapper.MemberMapper
import org.teamalilm.alilmbe.adapter.out.persistence.mapper.ProductMapper
import org.teamalilm.alilmbe.adapter.out.persistence.repository.basket.BasketRepository
import org.teamalilm.alilmbe.adapter.out.persistence.repository.basket.SpringDataBasketRepository
import org.teamalilm.alilmbe.application.port.out.Basket.AddBasketPort
import org.teamalilm.alilmbe.application.port.out.Basket.LoadBasketPort
import org.teamalilm.alilmbe.application.port.out.product.LoadBasketSlicePort
import org.teamalilm.alilmbe.application.port.out.product.ProductBasketCountProjection
import org.teamalilm.alilmbe.domain.basket.Basket
import org.teamalilm.alilmbe.domain.member.Member
import org.teamalilm.alilmbe.domain.product.Product

@Component
class BasketPersistenceAdapter(
    private val springDataBasketRepository: SpringDataBasketRepository,
    private val basketRepository: BasketRepository,
    private val basketMapper: BasketMapper,
    private val memberMapper: MemberMapper,
    private val productMapper: ProductMapper
) : AddBasketPort, LoadBasketPort, LoadBasketSlicePort {

    override fun addBasket(
        basket: Basket,
        member: Member,
        product: Product
    ): Basket {
        val basketJpaEntity = springDataBasketRepository.save(
                basketMapper.mapToJpaEntity(
                basket,
                memberMapper.mapToJpaEntity(member),
                productMapper.mapToJpaEntity(product)
            )
        )

        return basketMapper.mapToDomainEntity(basketJpaEntity)
    }

    override fun loadBasket(
        memberId: Member.MemberId,
        productId: Product.ProductId
    ): Basket? {
        val basketJpaEntity = springDataBasketRepository.findByMemberJpaEntityIdAndProductJpaEntityId(
            memberId = memberId,
            productId = productId
        )

        return basketMapper.mapToDomainEntityOrNull(basketJpaEntity)
    }

    override fun loadBasketSlice(pageRequest: PageRequest): Slice<ProductBasketCountProjection> {
        return basketRepository.loadBasketSlice(pageRequest)
    }

}