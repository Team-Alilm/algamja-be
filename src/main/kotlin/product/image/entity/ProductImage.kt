package org.team_alilm.product.image.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.team_alilm.common.jpa.base.BaseEntity

@Entity
@Table(
    name = "product_image",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_product_image_product_id_url",
            columnNames = ["product_id", "image_url"]
        )
    ],
    indexes = [
        Index(name = "idx_product_image_product_id", columnList = "product_id")
    ]
)
@Comment("상품 이미지")
class ProductImage(

    @field:Column(name = "image_url", nullable = false, length = 512) // ← @field:Column 중요
    @Comment("상품 이미지 URL")
    val imageUrl: String,

    @field:Column(name = "product_id", nullable = false)              // ← @field:Column 중요
    @Comment("상품 ID (FK 없이 단방향)")
    val productId: Long,

) : BaseEntity<Long>() {

    protected constructor() : this(
        imageUrl = "",
        productId = 0L
    )
}