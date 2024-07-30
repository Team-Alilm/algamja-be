package org.teamalilm.alilmbe.adapter.out.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "role")
class RoleJpaEntity {
    @Column(nullable = false, unique = true)
    var name: String? = null

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}