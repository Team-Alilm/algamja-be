package org.team_alilm.adapter.out.persistence.entity

import jakarta.persistence.*
import org.team_alilm.domain.Role

@Entity
@Table(name = "role")
open class RoleJpaEntity (
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    var roleType: Role.RoleType,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)