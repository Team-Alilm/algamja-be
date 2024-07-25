package org.teamalilm.alilmbe.adapter.out.persistence.repository.basket

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilmbe.adapter.out.persistence.entity.basket.BasketJpaEntity

interface SpringDataBasketRepository : JpaRepository<BasketJpaEntity, Long>