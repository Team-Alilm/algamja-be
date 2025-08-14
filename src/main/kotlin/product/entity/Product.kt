package org.team_alilm.product.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.Check
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLRestriction
import org.team_alilm.common.enums.Store
import org.team_alilm.common.jpa.base.BaseEntity
import java.math.BigDecimal

@Entity
@Table(
    name = "product",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_store_store_number", columnNames = ["store", "store_number"]),
        UniqueConstraint(
            name = "uk_product_options",
            columnNames = ["store", "store_number", "first_option", "second_option", "third_option"]
        )
    ],
    indexes = [
        Index(name = "idx_brand", columnList = "brand"),
        Index(name = "idx_category1_2", columnList = "first_category, second_category"),
        Index(name = "idx_price", columnList = "price")
    ]
)
@Check(constraints = "price >= 0")
@SQLRestriction("is_delete = false")
class Product(

    @Column(name = "store_number", nullable = false)
    @Comment("외부몰(무신사/지그재그/29CM) 내 상품 식별자")
    val storeNumber: Long,

    @Column(nullable = false, length = 200)
    @Comment("상품명")
    val name: String,

    @Column(nullable = false, length = 120)
    @Comment("브랜드명")
    val brand: String,

    @Column(name = "thumbnail_url", nullable = false, length = 512)
    @Comment("상품 썸네일 이미지 URL")
    val thumbnailUrl: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Comment("입점 쇼핑몰 (예: MUSINSA, ZIGZAG, 29CM)")
    val store: Store,

    @Column(name = "first_category", length = 80)
    @Comment("상품 1차 카테고리")
    val firstCategory: String,

    @Column(name = "second_category", length = 80)
    @Comment("상품 2차 카테고리 (없을 수 있음)")
    var secondCategory: String? = null,

    @Column(nullable = false, precision = 15, scale = 0)
    @Comment("상품 가격 (원 단위, 소수점 없음)")
    var price: BigDecimal,

    @Column(name = "first_option", length = 120)
    @Comment("상품 옵션1 (예: 색상)")
    var firstOption: String? = null,

    @Column(name = "second_option", length = 120)
    @Comment("상품 옵션2 (예: 사이즈)")
    var secondOption: String? = null,

    @Column(name = "third_option", length = 120)
    @Comment("상품 옵션3 (추가 옵션이 있을 경우)")
    var thirdOption: String? = null,
) : BaseEntity<Long>() {

    protected constructor() : this(
        storeNumber = 0L,
        name = "",
        brand = "",
        thumbnailUrl = "",
        store = Store.MUSINSA,
        firstCategory = "",
        secondCategory = null,
        price = BigDecimal.ZERO
    )

    fun toSlackMessage(): String = """
        {
            "text":"${this.name} 상품이 재 입고 되었습니다.
        
                상품명: ${this.name}
                상품번호: ${this.storeNumber}
                상품 옵션1: ${this.firstOption}
                상품 옵션2: ${this.secondOption}
                상품 옵션3: ${this.thirdOption}
                상품 구매링크 : ${this.store.url}
                바구니에서 삭제되었습니다.
                "
        }
    """.trimIndent()
}