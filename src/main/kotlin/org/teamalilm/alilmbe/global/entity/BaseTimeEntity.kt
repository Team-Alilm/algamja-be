package org.teamalilm.alilmbe.global.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTimeEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdDate: LocalDateTime? = null

    @LastModifiedDate
    @Column(nullable = false)
    var lastModifiedDate: LocalDateTime? = null

    @PrePersist
    fun PrePersist() {
        this.createdDate = LocalDateTime.now()
    }

    @PreUpdate
    fun preUpdate() {
        this.lastModifiedDate = LocalDateTime.now()
    }
}