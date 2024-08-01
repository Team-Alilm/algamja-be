package org.teamalilm.alilmbe.adapter.out.persistence.repository.basket

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.adapter.out.persistence.entity.BasketJpaEntity
import org.teamalilm.alilmbe.adapter.out.persistence.entity.MemberJpaEntity
import org.teamalilm.alilmbe.adapter.out.persistence.entity.ProductJpaEntity
import org.teamalilm.alilmbe.adapter.out.persistence.repository.BasketRepository
import org.teamalilm.alilmbe.adapter.out.persistence.repository.MemberRepository
import org.teamalilm.alilmbe.adapter.out.persistence.repository.ProductRepository
import org.teamalilm.alilmbe.domain.Product
import org.teamalilm.alilmbe.global.security.service.oAuth2.data.Provider

@DataJpaTest
@Transactional
class BasketRepositoryTest @Autowired constructor(
    private val basketRepository: BasketRepository,
    private val productRepository: ProductRepository,
    private val memberRepository: MemberRepository
) {

    @BeforeEach
    fun setUp() {
        // 테스트 데이터 준비
        val product1 = ProductJpaEntity(
            number = 35697L,
            name = "Product 1",
            brand = "Brand 1",
            imageUrl = "https://image.url",
            store = Product.Store.MUSINSA,
            category = "Category 1",
            price = 10000,
            option1 = "Option 1",
            option2 = "Option 2",
            option3 = "Option 3"
        )
        val product2 = ProductJpaEntity(
            number = 35687L,
            name = "Product 2",
            brand = "Brand 2",
            imageUrl = "https://image.url",
            store = Product.Store.MUSINSA,
            category = "Category 2",
            price = 10000,
            option1 = "Option 1",
            option2 = "Option 2",
            option3 = "Option 3"
        )
        productRepository.saveAll(listOf(product1, product2))

        val member1 = MemberJpaEntity(
            provider = Provider.KAKAO,
            providerId = 12345679L,
            email = "cloudwi@naver.com",
            phoneNumber = "01012345678",
            nickname = "cloudwi",
        )
        val member2 = MemberJpaEntity(
            provider = Provider.KAKAO,
            providerId = 123456789L,
            email = "cloudwi@naver.com,",
            phoneNumber = "0101236787",
            nickname = "cloudwi",
        )
        memberRepository.saveAll(listOf(member1, member2))

        basketRepository.save(BasketJpaEntity(memberJpaEntity = member1, productJpaEntity = product1))
        basketRepository.save(BasketJpaEntity(memberJpaEntity = member2, productJpaEntity = product1))
        basketRepository.save(BasketJpaEntity(memberJpaEntity = member1, productJpaEntity = product2))
    }

    @Test
    fun `findProductWithBasketCount should return correct product count`() {
        val result = basketRepository.loadBasketSlice(PageRequest.of(0, 10))

        // Validate results
        assert(result.content.isNotEmpty())

        result.content.forEach {
            val product = it.get("productJpaEntity", ProductJpaEntity::class.java)!!
            val count = it.get("waitingCount", Long::class.java)!!

            println("count: $count")
            println(
                "product: ${product.id}, ${product.number}, ${product.name}, ${product.brand}, ${product.imageUrl}, ${product.store}, ${product.category}, ${product.price}, ${product.option1}, ${product.option2}, ${product.option3}"
            )

            assert(product.id != null)
            assert(count > 0)
        }
    }

}