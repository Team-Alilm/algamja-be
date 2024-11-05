package org.team_alilm.adapter.out.persistence.entity

import jakarta.persistence.*
import org.team_alilm.global.jpa.base.BaseTimeEntity

@Entity
@Table(name = "member_role_mapping")
class MemberRoleMappingJpaEntity(

    @Column(nullable = false)
    val memberId: Long,

    @Column(nullable = false)
    val roleId: Long,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) : BaseTimeEntity()