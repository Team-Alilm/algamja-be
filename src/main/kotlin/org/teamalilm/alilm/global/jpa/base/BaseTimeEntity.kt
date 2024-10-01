package org.teamalilm.alilm.global.jpa.base

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTimeEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdDate: Long = 0

    @LastModifiedDate
    @Column(nullable = false)
    var lastModifiedDate: Long = 0

    @Column(nullable = false)
    var isDelete: Boolean = false

    fun delete() {
        this.isDelete = true
    }

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
}
