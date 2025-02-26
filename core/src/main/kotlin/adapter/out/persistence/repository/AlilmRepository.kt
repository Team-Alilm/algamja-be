package org.team_alilm.adapter.out.persistence.repository

import domain.Alilm
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.team_alilm.adapter.out.persistence.entity.AlilmJpaEntity
import org.team_alilm.adapter.out.persistence.entity.ProductJpaEntity
import org.team_alilm.adapter.out.persistence.repository.alilm.AlilmAllCountAndDailyCount
import org.team_alilm.adapter.out.persistence.repository.alilm.AlilmAndProductProjection
import org.team_alilm.adapter.out.persistence.repository.alilm.AlilmHistoryCountProjection
import org.team_alilm.application.port.out.LoadMyAlilmHistoryPort

interface AlilmRepository : JpaRepository<AlilmJpaEntity, Long> {

    @Query("""
        SELECT new org.team_alilm.adapter.out.persistence.repository.alilm.AlilmAllCountAndDailyCount(
            COUNT(*),
            SUM(CASE WHEN a.createdDate >= :startOfToday THEN 1 ELSE 0 END)
        )
        FROM AlilmJpaEntity a
    """)
    fun allCountAndDailyCount(@Param("startOfToday") startOfToday: Long): AlilmAllCountAndDailyCount

    @Query("""
    select new org.team_alilm.adapter.out.persistence.repository.alilm.AlilmAndProductProjection(
        a,
        p
    )
    from AlilmJpaEntity a
    join 
        ProductJpaEntity p 
        on a.productId = p.id
    where 
        a.memberId         = :memberId
        AND a.createdDate >= :dayLimit
    order by
        a.createdDate desc
    """)
    fun findAlilmAndProductByMemberId(
        memberId: Long,
        dayLimit: Long
    ) : List<AlilmAndProductProjection>

   @Query("""
        SELECT p
        FROM ProductJpaEntity p
        join AlilmJpaEntity a
        on a.productId = p.id
        group by p.id
        order by a.createdDate desc
        limit :count
    """)
    fun findByRestockRanking(count: Int): List<ProductJpaEntity>

    @Query("""
        SELECT 
               new org.team_alilm.adapter.out.persistence.repository.alilm.AlilmHistoryCountProjection(
                    count(a.id)
               )
        FROM   AlilmJpaEntity a
        WHERE  1 = 1
        AND    a.readYn      = false
        AND    a.memberId    = :memberId
        AND    a.createdDate >= :dayLimit
    """)
    fun findMyAlilmHistoryCount(memberId: Long, dayLimit: Long) : AlilmHistoryCountProjection
}


