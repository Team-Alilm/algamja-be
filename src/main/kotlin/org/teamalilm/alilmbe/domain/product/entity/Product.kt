package org.teamalilm.alilmbe.domain.product.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.teamalilm.alilmbe.global.jpa.base.BaseEntity

@Entity
@Table(
    uniqueConstraints = [UniqueConstraint(
        name = "tag_key_number_size_color",
        columnNames = ["store", "number", "option1", "option2", "option3"]
    )]
)
class Product(
    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "brand")
    val brand: String,

    @Column(name = "image_url")
    val imageUrl: String,

    @Column(name = "category")
    val category: String,

    @Column(name = "price")
    val price: Int,

    @Column(name = "waiting_count")
    var waitingCount: Long = 0,

    @Embedded
    val productInfo: ProductInfo,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseEntity() {

    @Embeddable
    class ProductInfo(
        @Column(name = "store", nullable = false)
        @Enumerated(EnumType.STRING)
        val store: Store,

        @Column(name = "number", nullable = false)
        val number: Int,

        @Column(name = "option1", nullable = false)
        val option1: String,

        @Column(name = "option2")
        val option2: String?,

        @Column(name = "option3")
        val option3: String?
    ) {

        enum class Store {

            MUSINSA, ZIGZAG, OLIVEYOUNG

        }

        override fun toString(): String {
            return "\n상품번호=$number\n옵션1='$option1'\n옵션2='$option2'\n옵션3='$option3'"
        }
    }

    override fun toString(): String {
        return "상품명 ='$name' \n상품 이미지 ='$imageUrl'\n상품 정보 =$productInfo\nid =$id"
    }

}