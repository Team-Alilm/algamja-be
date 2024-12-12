package org.team_alilm.adapter.out.persistence.repository.spring_data

import org.springframework.data.jpa.repository.JpaRepository
import org.team_alilm.adapter.out.persistence.entity.ProductJpaEntity

interface SpringDataProductRepository : JpaRepository<ProductJpaEntity,Long> {

    fun findByIdAndIsDeleteFalse(value: Long): ProductJpaEntity?

    fun findTop10ByFirstCategoryAndIsDeleteFalseOrderByCreatedDate(category: String): List<ProductJpaEntity>
}