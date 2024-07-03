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
import org.springframework.data.jpa.domain.AbstractPersistable_.id
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
    private val _name: String,

    @Column(name = "brand")
    private val _brand: String,

    @Column(name = "image_url")
    private val _imageUrl: String,

    @Column(name = "category")
    val _category: String,

    @Column(name = "price")
    val _price: Int,

    @Column(name = "waiting_count")
    val _waitingCount: Long = 0,

    @Embedded
    private val _productInfo: ProductInfo,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val _id: Long? = null
) : BaseEntity() {

    val name : String
        get() = _name

    val brand : String
        get() = _brand

    val imageUrl : String
        get() = _imageUrl

    val category : String
        get() = _category

    val price : Int
        get() = _price

    val waitingCount : Long
        get() = _waitingCount

    val id : Long?
        get() = _id

    @Embeddable
    class ProductInfo(
        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        private val _store: Store,

        @Column(nullable = false)
        private val _number: Number,

        @Column(nullable = false)
        private val _option1: String,

        @Column
        private val _option2: String?,

        @Column
        private val _option3: String?
    ) {

        val store: Store
            get() = _store

        val number: Number
            get() = _number

        val option1: String
            get() = _option1

        val option2: String?
            get() = _option2

        val option3: String?
            get() = _option3

        enum class Store {

            MUSINSA, ZIGZAG, OLIVEYOUNG

        }

        override fun toString(): String {
            return "\n상품번호=$number\n옵션1='$option1'\n옵션2='$option2'\n옵션3='$option3'"
        }
    }

    override fun toString(): String {
        return "상품명 ='$_name' \n상품 이미지 ='$_imageUrl'\n상품 정보 =$_productInfo\nid =$id"
    }

}