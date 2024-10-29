package org.team_alilm.adapter.out.persistence.adapter

import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.entity.BasketJpaEntity
import org.team_alilm.adapter.out.persistence.mapper.BasketMapper
import org.team_alilm.adapter.out.persistence.mapper.ProductMapper
import org.team_alilm.adapter.out.persistence.repository.BasketRepository
import org.team_alilm.adapter.out.persistence.repository.spring_data.SpringDataBasketRepository
import org.team_alilm.application.port.out.*
import org.team_alilm.domain.Basket
import org.team_alilm.domain.Member
import org.team_alilm.domain.Product
import org.team_alilm.global.error.NotFoundBasketException
import org.team_alilm.global.error.NotFoundMemberException
import java.time.LocalDate
import java.time.ZoneOffset


@Component
class BasketAdapter(
    private val springDataBasketRepository: SpringDataBasketRepository,
    private val basketRepository: BasketRepository,
    private val basketMapper: BasketMapper,
    private val productMapper: ProductMapper,
) : AddBasketPort,
    LoadBasketPort,
    LoadMyBasketsPort,
    LoadAllAndDailyCountPort,
    DeleteBasketPort
{

    private val log = LoggerFactory.getLogger(javaClass)

    override fun addBasket(
        basket: Basket,
        member: Member,
        product: Product
    ): Basket {
        val basketJpaEntity = basketJpaEntity(basket, member, product)
        springDataBasketRepository.save(basketJpaEntity)

        return basketMapper.mapToDomainEntity(basketJpaEntity)
    }

    override fun loadBasket(
        memberId: Member.MemberId,
        productId: Product.ProductId
    ): Basket? {
        val basketJpaEntity = springDataBasketRepository.findByMemberIdAndIsDeleteFalseAndProductId(
            memberId = memberId.value,
            productId = productId.value
        )

        return basketMapper.mapToDomainEntityOrNull(basketJpaEntity)
    }

    override fun loadMyBasket(memberId: Member.MemberId): List<Basket> {
        val basketJpaEntityList = springDataBasketRepository.findByMemberIdAndIsDeleteFalse(memberId.value)

        return basketJpaEntityList.map { basketMapper.mapToDomainEntity(it) }
    }

    override fun loadBasket(productId: Product.ProductId): List<Basket> {
        val basketJpaEntityList = springDataBasketRepository.findByProductIdAndIsDeleteFalseAndIsAlilmFalse(productId.value)

        return basketJpaEntityList.map { basketMapper.mapToDomainEntity(it) }
    }

    override fun loadBasket(productNumber: Number): List<Basket> {
        val basketJpaEntityList = basketRepository.findByProductNumber(productNumber)

        return basketJpaEntityList.map { basketMapper.mapToDomainEntity(it) }
    }

    override fun loadMyBaskets(member: Member) : List<LoadMyBasketsPort.BasketAndProduct> {
        return basketRepository
            .myBasketList(member.id?.value ?: throw NotFoundMemberException())
            .map {
                LoadMyBasketsPort.BasketAndProduct(
                    basket = basketMapper.mapToDomainEntity(it.basketJpaEntity),
                    product = productMapper.mapToDomainEntity(it.productJpaEntity),
                    waitingCount = it.waitingCount
                )
            }
    }

    override fun getAllAndDailyCount(): LoadAllAndDailyCountPort.AllAndDailyCount {
        val today = LocalDate.now(ZoneOffset.UTC)
        val midnight = today.atStartOfDay(ZoneOffset.UTC)
        val midnightMillis = midnight.toInstant().toEpochMilli()

        val allIsAlilmTrueBaskets = springDataBasketRepository.findByIsAlilmTrueAndIsDeleteFalse()
        val dailyAlilmTrueBaskets = springDataBasketRepository.findByIsAlilmTrueAndAlilmDateGreaterThanEqualAndIsDeleteFalse(midnightMillis)

        return LoadAllAndDailyCountPort.AllAndDailyCount(
            allCount = allIsAlilmTrueBaskets.size.toLong(),
            dailyCount = dailyAlilmTrueBaskets.size.toLong()
        )
    }

    private fun basketJpaEntity(
        basket: Basket,
        member: Member,
        product: Product
    ): BasketJpaEntity {
        val basketJpaEntity = basketMapper
            .mapToJpaEntity(
                basket,
                member.id?.value ?: throw NotFoundMemberException(),
                product.id?.value ?: throw NotFoundBasketException()
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