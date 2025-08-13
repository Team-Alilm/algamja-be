package org.team_alilm.product.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.team_alilm.product.entity.Product

interface ProductRepository : JpaRepository<Product, Long> {

    fun findTop10ByIdNotAndFirstCategoryOrSecondCategoryAndDeletedFalseOrderByIdDesc(
        id: Long,
        firstCategory: String,
        secondCategory: String?
    ): List<Product>
}