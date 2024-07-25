package org.teamalilm.alilmbe.adapter.out.persistence.entity.basket

import jakarta.persistence.*
import org.teamalilm.alilmbe.adapter.out.persistence.entity.member.MemberJpaEntity
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.ProductJpaEntity
import org.teamalilm.alilmbe.global.jpa.base.BaseEntity

@Entity
@Table(
    name = "basket",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["member_id", "product_id"]
        )
    ]
)
class BasketJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val memberJpaEntity: MemberJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val productJpaEntity: ProductJpaEntity

) : BaseEntity()