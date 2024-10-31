package org.team_alilm.adapter.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.team_alilm.adapter.out.persistence.entity.AlilmJpaEntity
import org.team_alilm.adapter.out.persistence.repository.alilm.AlilmAllCountAndDailyCount

interface AlilmRepository : JpaRepository<AlilmJpaEntity, Long> {

    @Query("""SELECT new org.team_alilm.adapter.out.persistence.repository.alilm.AlilmAllCountAndDailyCount(
        COUNT(*),
        SUN(CASE WHEN a.createdDate >= :startOfToday THEN 1 ELSE 0 END)
        )
        FROM AlilmJpaEntity a
    """)
    fun allCountAndDailyCount(startOfToday: Long): AlilmAllCountAndDailyCount
}