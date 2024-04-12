package org.teamalilm.alilmbe.domain.basket.repository.data

import org.teamalilm.alilmbe.domain.product.entity.Store

data class CountBasketsGroupByProductIdWithProduct(
    val id: Long, // 상품 ID
    val count: Long, // 해당 상품의 장바구니 수량
    val name: String, // 상품명
    val imageUrl: String, // 상품 이미지 URL
    val store: Store, // 상품 소속 매장
    val number: String, // 상품 번호
    val option1: String, // 상품 옵션1
    val option2: String?, // 상품 옵션2 (nullable)
    val option3: String? // 상품 옵션3 (nullable)
)