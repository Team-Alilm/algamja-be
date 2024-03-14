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
import java.time.ZoneId
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime
import java.util.TimeZone

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTimeEntity(
    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdDate: ZonedDateTime = ZonedDateTime.of(LocalDateTime.now(), UTC),

    @LastModifiedDate
    @Column(nullable = false)
    var lastModifiedDate: ZonedDateTime = ZonedDateTime.of(LocalDateTime.now(), UTC)
) {

    @PrePersist
    private fun PrePersist() {
        this.createdDate = ZonedDateTime.of(LocalDateTime.now(), UTC)
    }

    @PreUpdate
    private fun preUpdate() {
        this.lastModifiedDate = ZonedDateTime.of(LocalDateTime.now(), UTC)
    }

}