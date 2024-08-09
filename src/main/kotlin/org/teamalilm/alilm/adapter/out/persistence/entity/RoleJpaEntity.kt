package org.teamalilm.alilm.adapter.out.persistence.entity

import jakarta.persistence.*
import org.teamalilm.alilm.domain.Role

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