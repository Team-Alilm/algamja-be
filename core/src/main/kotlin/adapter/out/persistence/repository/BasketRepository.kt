package org.team_alilm.adapter.out.persistence.repository

import jakarta.persistence.Tuple
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.team_alilm.adapter.out.persistence.entity.BasketJpaEntity
import org.team_alilm.adapter.out.persistence.entity.MemberJpaEntity

interface BasketRepository : JpaRepository<BasketJpaEntity, Long> {

    @Query(
        """
            SELECT p as productJpaEntity, COUNT(b) as waitingCount
            FROM BasketJpaEntity b
            JOIN b.productJpaEntity p
            on b.productJpaEntity.id = p.id
            where b.isDelete = false
            and p.isDelete = false
            and b.isAlilm = false
            and b.isHidden = false
            GROUP BY p.id
            ORDER BY COUNT(b) DESC, b.id ASC
        """
    )
    fun loadBasketSlice(pageRequest: PageRequest): Slice<Tuple>

    @Query("""
        SELECT b as basketJpaEntity, COUNT(b) as waitingCount
        FROM BasketJpaEntity b
        JOIN b.productJpaEntity p
        on b.productJpaEntity.id = p.id
        where b.isDelete = false
        and b.memberJpaEntity = :memberJpaEntity
        and b.isDelete = false
        and p.isDelete = false
        group by b.productJpaEntity.id
        ORDER BY b.id DESC
    """)
    fun myBasketList(memberJpaEntity: MemberJpaEntity): List<Tuple>
}

