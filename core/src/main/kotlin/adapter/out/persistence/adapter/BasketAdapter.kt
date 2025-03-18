package org.team_alilm.adapter.out.persistence.adapter

import domain.Basket
import domain.Member
import domain.product.ProductId
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.adapter.data.ProductAndBasket
import org.team_alilm.adapter.out.persistence.entity.BasketJpaEntity
import org.team_alilm.adapter.out.persistence.mapper.BasketMapper
import org.team_alilm.adapter.out.persistence.mapper.ProductMapper
import org.team_alilm.adapter.out.persistence.repository.BasketRepository
import org.team_alilm.adapter.out.persistence.repository.product.ProductAndWaitingCount
import org.team_alilm.adapter.out.persistence.repository.spring_data.SpringDataBasketRepository
import org.team_alilm.application.port.out.*
import org.team_alilm.global.error.NotFoundBasketException
import org.team_alilm.global.error.NotFoundMemberException

@Component
class BasketAdapter(
    private val springDataBasketRepository: SpringDataBasketRepository,
    private val basketRepository: BasketRepository,
    private val basketMapper: BasketMapper,
    private val productMapper: ProductMapper,
) : AddBasketPort,
    LoadBasketPort,
    LoadMyBasketsPort,
    DeleteBasketPort
{

    override fun addBasket(
        basket: Basket,
        memberId: Member.MemberId,
        productId: ProductId
    ): Basket {
        val basketJpaEntity = basketJpaEntity(basket, memberId, productId)
        springDataBasketRepository.save(basketJpaEntity)

        return basketMapper.mapToDomainEntity(basketJpaEntity)
    }

    override fun loadBasketIncludeIsDelete(
        memberId: Member.MemberId,
        productId: ProductId
    ): Basket? {
        val basketJpaEntity = springDataBasketRepository.findByMemberIdAndProductId(
            memberId = memberId.value,
            productId = productId.value
        )

        return basketMapper.mapToDomainEntityOrNull(basketJpaEntity)
    }

    override fun loadMyBasket(memberId: Member.MemberId): List<Basket> {
        val basketJpaEntityList = springDataBasketRepository.findByMemberIdAndIsDeleteFalse(memberId.value)

        return basketJpaEntityList.map { basketMapper.mapToDomainEntity(it) }
    }

    override fun loadOldBasket(memberId: Member.MemberId): ProductAndBasket {
        val productAndBasket = basketRepository.findByMemberId(memberId.value)

        return ProductAndBasket(
            product = productMapper.mapToDomainEntity(productAndBasket.productJpaEntity),
            basket = basketMapper.mapToDomainEntity(productAndBasket.basketJpaEntity)
        )
    }

    override fun loadBasketIncludeIsDelete(productId: ProductId): List<Basket> {
        val basketJpaEntityList = springDataBasketRepository.findByProductIdAndIsDeleteFalseAndIsAlilmFalse(productId.value)

        return basketJpaEntityList.map { basketMapper.mapToDomainEntity(it) }
    }

    override fun loadBasketIncludeIsDelete(productNumber: Number): List<Basket> {
        val basketJpaEntityList = basketRepository.findByProductNumber(productNumber)

        return basketJpaEntityList.map { basketMapper.mapToDomainEntity(it) }
    }

    override fun loadBasketCount(productId: ProductId): Long {
        return springDataBasketRepository.countByProductIdAndIsAlilmFalseAndIsDeleteFalse(productId.value)
    }

    override fun loadBasketList(productId: ProductId): List<Basket> {
        val basketJpaEntityList = springDataBasketRepository.findAllByProductIdAndIsAlilmFalseAndIsDeleteFalse(productId.value)

        return basketJpaEntityList.map { basketMapper.mapToDomainEntity(it) }
    }

    override fun loadBasketSlice(pageRequest: PageRequest): Slice<ProductAndWaitingCount> {
        return basketRepository.findAllByWaitingCount(pageRequest).map {
            ProductAndWaitingCount(
                product = productMapper.mapToDomainEntity(it.productJpaEntity),
                waitingCount = it.waitingCount
            )
        }
    }

    override fun loadMyBaskets(member: Member) : List<LoadMyBasketsPort.BasketAndProduct> {
        return basketRepository
            .myBasketList(member.id?.value ?: throw NotFoundMemberException())
            .map {
                LoadMyBasketsPort.BasketAndProduct(
                    basket = basketMapper.mapToDomainEntity(it.basketJpaEntity),
                    product = productMapper.mapToDomainEntity(it.productJpaEntity),
                )
            }
    }

    private fun basketJpaEntity(
        basket: Basket,
        memberId: Member.MemberId,
        productId: ProductId
    ): BasketJpaEntity {
        val basketJpaEntity = basketMapper
            .mapToJpaEntity(
                basket,
                memberId.value,
                productId.value
            )

        return basketJpaEntity
    }

    override fun deleteBasket(memberId: Long, basketId: Long) {
        val basketJpaEntity = springDataBasketRepository.findByIdAndMemberId(
            basketId = basketId,
            memberId = memberId
        ) ?: throw NotFoundBasketException()

        basketJpaEntity.delete()
    }

}