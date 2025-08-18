package org.team_alilm.common.jpa.base

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity<ID : Serializable> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: ID? = null

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    open var createdDate: Long = 0
        protected set

    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = false)
    open var lastModifiedDate: Long = 0
        protected set

    @Column(nullable = false)
    open var isDelete: Boolean = false
        protected set

    @PrePersist
    fun prePersist() {
        val now = System.currentTimeMillis()
        createdDate = now
        lastModifiedDate = now
    }

    @PreUpdate
    fun preUpdate() {
        lastModifiedDate = System.currentTimeMillis()
    }

    fun delete() {
        this.isDelete = true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as BaseEntity<*>
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: System.identityHashCode(this)
}