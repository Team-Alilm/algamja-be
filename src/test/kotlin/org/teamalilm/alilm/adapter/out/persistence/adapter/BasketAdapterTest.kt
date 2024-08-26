package org.teamalilm.alilm.adapter.out.persistence.adapter

import org.junit.jupiter.api.Test

import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.teamalilm.alilm.adapter.out.persistence.entity.BasketJpaEntity
import org.teamalilm.alilm.adapter.out.persistence.entity.MemberJpaEntity
import org.teamalilm.alilm.adapter.out.persistence.entity.ProductJpaEntity
import org.teamalilm.alilm.adapter.out.persistence.mapper.BasketMapper
import org.teamalilm.alilm.adapter.out.persistence.mapper.MemberMapper
import org.teamalilm.alilm.adapter.out.persistence.mapper.ProductMapper
import org.teamalilm.alilm.adapter.out.persistence.repository.BasketRepository
import org.teamalilm.alilm.adapter.out.persistence.repository.spring_data.SpringDataBasketRepository
import org.teamalilm.alilm.domain.Product
import org.teamalilm.alilm.global.security.service.oAuth2.data.Provider
import java.time.LocalDate
import java.time.ZoneOffset

class BasketAdapterTest {

    private val springDataBasketRepository: SpringDataBasketRepository = mock()
    private val basketRepository: BasketRepository = mock()
    private val basketMapper: BasketMapper = mock()
    private val productMapper: ProductMapper = mock()
    private val memberMapper: MemberMapper = mock()

    private val basketAdapter = BasketAdapter(
        springDataBasketRepository = springDataBasketRepository,
        basketRepository = basketRepository,
        basketMapper = basketMapper,
        productMapper = productMapper,
        memberMapper = memberMapper
    )

    @Test
    fun getAllAndDailyCount() {
        // Arrange
        val today = LocalDate.now()
        val midnight = today.atStartOfDay()
        val midnightMillis = midnight.toInstant(ZoneOffset.UTC).toEpochMilli()

        // Mock data
        val allBaskets = listOf(
            basketJpaEntity(midnightMillis - 1000L),
            basketJpaEntity(midnightMillis + 1000L)
        )

        val dailyBaskets = listOf(
            basketJpaEntity(
                midnightMillis + 1000L
            )
        )

        `when`(springDataBasketRepository.findByIsAlilmTrueAndIsDeleteFalse()).thenReturn(allBaskets)
        `when`(springDataBasketRepository.findByIsAlilmTrueAndAlilmDateGreaterThanEqualAndIsDeleteFalse(midnightMillis))
            .thenReturn(dailyBaskets)

        val result = basketAdapter.getAllAndDailyCount()

        // Assert
        assert(result.allCount == allBaskets.size.toLong())
        assert(result.dailyCount == dailyBaskets.size.toLong())
    }


    private fun basketJpaEntity(midnightMillis: Long) = BasketJpaEntity(
        id = 1L,
        memberJpaEntity = memberJpaEntity(),
        productJpaEntity = productJpaEntity(),
        isAlilm = true,
        alilmDate = midnightMillis,
        isHidden = false
    )

    private fun memberJpaEntity() = MemberJpaEntity(
        id = 1L,
        provider = Provider.KAKAO,
        providerId = 1L,
        email = "",
        phoneNumber = "01012345678",
        nickname = "nickname"
    )

    private fun productJpaEntity() = ProductJpaEntity(
        number = 1L,
        name = "name",
        brand = "brand",
        imageUrl = "imageUrl",
        store = Product.Store.MUSINSA,
        category = "category",
        price = 10000,
        firstOption = "firstOption",
        secondOption = null,
        thirdOption = null
    )
}