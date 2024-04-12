package org.teamalilm.alilmbe.domain.basket.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.teamalilm.alilmbe.domain.basket.entity.Basket
import org.teamalilm.alilmbe.domain.basket.repository.data.CountBasketsGroupByProductIdWithProduct
import org.teamalilm.alilmbe.domain.member.entity.Member
import org.teamalilm.alilmbe.domain.product.entity.Product

interface BasketRepository : JpaRepository<Basket, Long> {
    fun existsByProductAndMember(product: Product, member: Member): Boolean

    fun findAllByProductId(productId: Long): List<Basket>

    @Query(
        """
        SELECT b
        FROM Basket b
        join fetch b.product
        join fetch b.member
        GROUP BY b.product.id
    """
    )
    fun findAllByGroupByProductId(): List<Basket>

    // 상품 ID로 그룹화된 장바구니 정보와 상품 정보 조회
    @Query(
        """
        SELECT b.product.id as id, 
               COUNT(b) as count,
               p.name as name,
               p.imageUrl as imageUrl,
               p.productInfo.store as store,
               p.productInfo.number as number,
               p.productInfo.option1 as option1,
               p.productInfo.option2 as option2,
               p.productInfo.option3 as option3
        FROM Basket b
        JOIN b.product p
        GROUP BY b.product.id, 
                 p.name,
                 p.imageUrl,
                 p.productInfo.store,
                 p.productInfo.number,
                 p.productInfo.option1,
                 p.productInfo.option2,
                 p.productInfo.option3
    """
    )
    fun countBasketsGroupByProductIdWithProduct(pageable: Pageable): Slice<CountBasketsGroupByProductIdWithProduct>
}