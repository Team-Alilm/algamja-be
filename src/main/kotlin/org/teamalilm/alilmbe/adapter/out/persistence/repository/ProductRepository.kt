package org.teamalilm.alilmbe.adapter.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.ProductJpaEntity

interface ProductRepository : JpaRepository<ProductJpaEntity, Long> {

}