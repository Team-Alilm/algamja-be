package org.teamalilm.alilmbe.global.jpa.base

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity : BaseTimeEntity() {

    @CreatedBy
    @Column(nullable = false, updatable = false)
    var createdBy: Long = 0

    @LastModifiedBy
    @Column(nullable = false)
    var lastModifiedBy: Long = 0

    // 삭제 여부
    @Column(nullable = false)
    var isDelete: Boolean = false

}