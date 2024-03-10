package org.teamalilm.alilmbe.domain.basket.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import org.teamalilm.alilmbe.domain.member.entity.Member
import org.teamalilm.alilmbe.domain.product.entity.Product

@Entity
class Basket(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null,

    @OneToMany
    @JoinColumn(name = "member_id")
    private val _members: MutableList<Member> = mutableListOf(),

    @OneToMany
    @JoinColumn(name = "product_id")
    private val _products: MutableList<Product> = mutableListOf()
) {

    val members
        get() = _members

    val products
        get() = _products
}