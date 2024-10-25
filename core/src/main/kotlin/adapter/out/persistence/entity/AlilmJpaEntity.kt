package org.team_alilm.adapter.out.persistence.entity

import jakarta.persistence.*
import org.team_alilm.global.jpa.base.BaseTimeEntity

@Entity
@Table(
    name = "alilm",
    indexes = [
        Index(
            name = "idx_product_id",
            columnList = "product_id"
        ),
        Index(
            name = "idx_member_id",
            columnList = "member_id"
        ),
        Index(
            name = "idx_product_member",
            columnList = "product_id, member_id"
        )
    ]
)
open class AlilmJpaEntity(

    @Column(nullable = false)
    val productId: Long,

    @Column(nullable = false)
    val memberId: Long,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) : BaseTimeEntity()