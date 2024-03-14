package org.teamalilm.alilmbe.domain.product.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilmbe.domain.member.entity.Member
import org.teamalilm.alilmbe.domain.product.entity.Product
import org.teamalilm.alilmbe.domain.product.entity.ProductInfo

interface ProductRepository : JpaRepository<Product, Long> {
    fun findAllByOrderByCreatedDateDesc(): List<Product>
    fun findByProductInfo(productInfo: ProductInfo): Product? {
        TODO("Not yet implemented")
    }
}