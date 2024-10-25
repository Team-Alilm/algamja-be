package org.team_alilm.global.jpa.base

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTimeEntity {

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
}


