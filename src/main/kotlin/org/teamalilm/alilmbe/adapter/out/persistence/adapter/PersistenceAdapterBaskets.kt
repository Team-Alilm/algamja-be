package org.teamalilm.alilmbe.adapter.out.persistence.adapter

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.teamalilm.alilmbe.adapter.out.persistence.entity.ProductJpaEntity
import org.teamalilm.alilmbe.adapter.out.persistence.mapper.BasketMapper
import org.teamalilm.alilmbe.adapter.out.persistence.mapper.MemberMapper
import org.teamalilm.alilmbe.adapter.out.persistence.mapper.ProductMapper
import org.teamalilm.alilmbe.adapter.out.persistence.repository.BasketRepository
import org.teamalilm.alilmbe.adapter.out.persistence.repository.spring_data.SpringDataBasketRepository
import org.teamalilm.alilmbe.application.port.out.*
import org.teamalilm.alilmbe.application.port.out.LoadAllBasketsPort.*
import org.teamalilm.alilmbe.application.port.out.LoadMyBasketsPort.*
import org.teamalilm.alilmbe.domain.Basket
import org.teamalilm.alilmbe.domain.Member
import org.teamalilm.alilmbe.domain.Member.*
import org.teamalilm.alilmbe.domain.Product
import org.teamalilm.alilmbe.domain.Product.*

@Component
class PersistenceAdapterBaskets(
    private val springDataBasketRepository: SpringDataBasketRepository,
    private val basketRepository: BasketRepository,
    private val basketMapper: BasketMapper,
    private val productMapper: ProductMapper,
    private val memberMapper: MemberMapper
) :
    AddBasketPort,
    LoadBasketPort,
    LoadSliceBasketPort,
    LoadMyBasketsPort,
    LoadAllBasketsPort,
    UpdateBasketPort{

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
        memberId: MemberId,
        productId: ProductId
    ): Basket? {
        val basketJpaEntity = springDataBasketRepository.findByMemberJpaEntityIdAndProductJpaEntityIdAndIsDeleteFalse(
            memberJpaEntityId = memberId.value,
            productJpaEntityId = productId.value
        )

        return basketMapper.mapToDomainEntityOrNull(basketJpaEntity)
    }

    override fun loadBasketSlice(pageRequest: PageRequest): Slice<LoadSliceBasketPort.BasketCountData> {
        val basketCountProjectionSlice = basketRepository.loadBasketSlice(pageRequest)

        return basketCountProjectionSlice.map {
            val productJpaEntity = it.get("productJpaEntity", ProductJpaEntity::class.java)!!
            val waitingCount = it.get("waitingCount", Long::class.java)!!

            LoadSliceBasketPort.BasketCountData(
                product = productMapper.mapToDomainEntity(productJpaEntity),
                waitingCount = waitingCount
            )
        }
    }

    override fun loadMyBaskets(member: Member) : List<BasketAndProduct> {
        return springDataBasketRepository.findAllByMemberJpaEntityAndIsDeleteFalse(
            memberMapper.mapToJpaEntity(member)
        ).map { BasketAndProduct(
            basket = basketMapper.mapToDomainEntity(it),
            product = productMapper.mapToDomainEntity(it.productJpaEntity)
        ) }
    }

    override fun loadAllBaskets() : List<BasketAndMemberAndProduct> {
        return springDataBasketRepository.findAllByIsDeleteFalse().map { BasketAndMemberAndProduct(
            basket = basketMapper.mapToDomainEntity(it),
            member = memberMapper.mapToDomainEntity(it.memberJpaEntity),
            product = productMapper.mapToDomainEntity(it.productJpaEntity)
        ) }
    }

    override fun deleteBasket(basketId: Basket.BasketId) {
        val basketJpaEntity = basketRepository.findByIdOrNull(basketId.value)
            ?: throw IllegalArgumentException("Basket not found")

        basketJpaEntity.delete()
        basketRepository.save(basketJpaEntity)
    }

}