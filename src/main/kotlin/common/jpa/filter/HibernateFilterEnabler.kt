package org.team_alilm.common.jpa.filter

import jakarta.persistence.EntityManager
import org.hibernate.Session
import org.springframework.stereotype.Component

@Component
class HibernateFilterEnabler(
    private val entityManager: EntityManager
) {
    fun enableNotDeletedOnly() {
        entityManager.unwrap(Session::class.java)
            .enableFilter("deletedFilter")
            .setParameter("isDeleted", false)
    }

    fun disableDeletedFilter() {
        entityManager.unwrap(Session::class.java)
            .disableFilter("deletedFilter")
    }
}