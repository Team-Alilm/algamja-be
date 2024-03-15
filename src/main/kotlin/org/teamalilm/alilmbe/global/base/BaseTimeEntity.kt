package org.teamalilm.alilmbe.global.base

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTimeEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    lateinit var createdDate: ZonedDateTime

    @LastModifiedDate
    @Column(nullable = false)
    lateinit var lastModifiedDate: ZonedDateTime

    @PrePersist
    fun prePersist() {
        val now = ZonedDateTime.now(UTC)
        createdDate = now
        lastModifiedDate = now
    }

    @PreUpdate
    fun preUpdate() {
        lastModifiedDate = ZonedDateTime.now(UTC)
    }
    
}