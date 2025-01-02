package org.team_alilm.adapter.out.persistence.entity

import domain.Role
import jakarta.persistence.*

@Entity
@Table(name = "role")
class RoleJpaEntity (
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    var roleType: Role.RoleType,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)