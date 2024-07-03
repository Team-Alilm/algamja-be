package org.teamalilm.alilmbe.domain.basket.entity

import jakarta.persistence.*
import org.teamalilm.alilmbe.domain.member.entity.Member
import org.teamalilm.alilmbe.domain.product.entity.Product
import org.teamalilm.alilmbe.global.jpa.base.BaseEntity

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["member_id", "product_id"])])
class Basket(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product

) : BaseEntity()