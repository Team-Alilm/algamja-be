package org.teamalilm.alilmbe.adapter.out.persistence.entity

import jakarta.persistence.*
import org.teamalilm.alilmbe.domain.Role

@Entity
@Table(name = "member_role")
class RoleJpaEntity (
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    var roleType: Role.RoleType,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)