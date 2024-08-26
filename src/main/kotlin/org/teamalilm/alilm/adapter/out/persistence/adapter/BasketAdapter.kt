package org.teamalilm.alilm.adapter.out.persistence.adapter

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component
import org.teamalilm.alilm.adapter.out.persistence.entity.BasketJpaEntity
import org.teamalilm.alilm.adapter.out.persistence.entity.ProductJpaEntity
import org.teamalilm.alilm.adapter.out.persistence.mapper.BasketMapper
import org.teamalilm.alilm.adapter.out.persistence.mapper.MemberMapper
import org.teamalilm.alilm.adapter.out.persistence.mapper.ProductMapper
import org.teamalilm.alilm.adapter.out.persistence.repository.BasketRepository
import org.teamalilm.alilm.adapter.out.persistence.repository.spring_data.SpringDataBasketRepository
import org.teamalilm.alilm.application.port.out.*
import org.teamalilm.alilm.application.port.out.LoadAllBasketsPort.*
import org.teamalilm.alilm.application.port.out.LoadMyBasketsPort.*
import org.teamalilm.alilm.domain.Basket
import org.teamalilm.alilm.domain.Member
import org.teamalilm.alilm.domain.Member.*
import org.teamalilm.alilm.domain.Product
import org.teamalilm.alilm.domain.Product.*
import java.time.LocalDate
import java.time.ZoneOffset


@Component
class BasketAdapter(
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
    SendAlilmBasketPort,
    LoadAllAndDailyCountPort {

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
        return springDataBasketRepository.findAllByMemberJpaEntityAndIsDeleteFalseAndOrderByCreatedDateDesc(
            memberMapper.mapToJpaEntity(member)
        ).map { BasketAndProduct(
            basket = basketMapper.mapToDomainEntity(it),
            product = productMapper.mapToDomainEntity(it.productJpaEntity)
        ) }
    }

    override fun loadAllBaskets() : List<BasketAndMemberAndProduct> {
        return springDataBasketRepository.findAllByIsDeleteFalseAndIsAlilmFalse().map { BasketAndMemberAndProduct(
            basket = basketMapper.mapToDomainEntity(it),
            member = memberMapper.mapToDomainEntity(it.memberJpaEntity),
            product = productMapper.mapToDomainEntity(it.productJpaEntity)
        ) }
    }

    override fun sendAlilmBasket(basket: Basket, member: Member, product: Product) {
        val basketJpaEntity = basketJpaEntity(basket, member, product)

        basketRepository.save(basketJpaEntity)
    }

    override fun getAllAndDailyCount(): LoadAllAndDailyCountPort.AllAndDailyCount {
        val today = LocalDate.now()
        val midnight = today.atStartOfDay()
        val midnightMillis = midnight.toInstant(ZoneOffset.UTC).toEpochMilli()

        val allIsAlilmTrueBaskets = springDataBasketRepository.findByIsAlilmTrue()
        val dailyAlilmTrueBaskets = springDataBasketRepository.findByIsAlilmTrueAndAlilmDateGreaterThanEqual(midnightMillis)

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
                memberMapper.mapToJpaEntity(member),
                productMapper.mapToJpaEntity(product)
            )

        return basketJpaEntity
    }

}