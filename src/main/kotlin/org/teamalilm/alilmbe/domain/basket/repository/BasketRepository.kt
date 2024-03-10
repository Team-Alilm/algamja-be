package org.teamalilm.alilmbe.domain.basket.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilmbe.domain.basket.entity.Basket

interface BasketRepository : JpaRepository<Basket, Long>