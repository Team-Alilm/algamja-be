package org.team_alilm.member.repository

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.common.enums.Provider
import org.team_alilm.member.entity.MemberTable
import org.team_alilm.member.entity.dto.MemberRow

@Repository
class MemberExposedRepository {

    // ---------- READ ----------
    fun fetchById(id: Long): MemberRow? =
        MemberTable
            .selectAll()
            .where {
                (MemberTable.id eq EntityID(id, MemberTable)) and
                        (MemberTable.isDelete eq false)
            }
            .limit(1)
            .firstOrNull()
            ?.let(MemberRow::from)

    fun fetchByProviderAndProviderId(provider: Provider, providerId: String): MemberRow? =
        MemberTable
            .select {
                (MemberTable.provider eq provider) and
                        (MemberTable.providerId eq providerId) and
                        (MemberTable.isDelete eq false)
            }
            .singleOrNull()
            ?.let(MemberRow::from)

    fun fetchPage(limit: Int, offset: Long): List<MemberRow> =
        MemberTable
            .select { MemberTable.isDelete eq false }
            .orderBy(MemberTable.id to SortOrder.DESC)
            .limit(n = limit, offset = offset)
            .map(MemberRow::from)

    fun existsByProviderAndProviderId(provider: Provider, providerId: String): Boolean =
        MemberTable
            .select {
                (MemberTable.provider eq provider) and
                        (MemberTable.providerId eq providerId) and
                        (MemberTable.isDelete eq false)
            }
            .limit(1)
            .empty()
            .not()

    fun countActive(): Long =
        MemberTable
            .slice(MemberTable.id.count())
            .select { MemberTable.isDelete eq false }
            .first()[MemberTable.id.count()]
            .toLong()

    // ---------- WRITE ----------
    fun create(
        provider: Provider,
        providerId: String,
        email: String,
        nickname: String
    ): Long {
        val stmt = MemberTable.insertAudited {
            it[this.provider]   = provider
            it[this.providerId] = providerId
            it[this.email]      = email
            it[this.nickname]   = nickname
            it[this.isDelete]   = false
        }
        return stmt[MemberTable.id].value
    }

    fun updateNickname(id: Long, nickname: String): Int =
        MemberTable.updateAudited({ MemberTable.id eq id }) {
            it[this.nickname] = nickname
        }

    override fun softDelete(id: Long): Int =
        MemberTable.softDeleteById(id)

    // ---------- UPSERT (옵션: MySQL일 때 사용) ----------
    fun upsertByProviderAndProviderId(
        provider: Provider,
        providerId: String,
        email: String,
        nickname: String
    ): Long {
        val stmt = MemberTable.insertAudited {
            it[this.provider]   = provider
            it[this.providerId] = providerId
            it[this.email]      = email
            it[this.nickname]   = nickname
            it[this.isDelete]   = false
        }
        // MySQL에서만 동작 (uniqueIndex(provider, providerId) 필요)
        stmt.onDuplicateKeyUpdate(
            MemberTable.email to email,
            MemberTable.nickname to nickname,
            MemberTable.isDelete to false,
            // updatedDate는 insertAudited/updateAudited에서 자동세팅되지만
            // upsert의 update 경로에도 반영하려면 명시해준다
            MemberTable.updatedDate to System.currentTimeMillis()
        )
        return stmt[MemberTable.id].value
    }
}