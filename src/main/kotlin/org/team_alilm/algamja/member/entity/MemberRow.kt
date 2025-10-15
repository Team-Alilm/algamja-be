package org.team_alilm.algamja.member.entity

import org.jetbrains.exposed.sql.ResultRow
import org.team_alilm.algamja.common.enums.Provider

data class MemberRow(
    val id: Long?,
    val provider: Provider,
    val providerId: String,
    val email: String,
    val nickname: String,
    val isDelete: Boolean,
    val createdAt: Long,
    val updatedAt: Long
) {

    companion object {
        fun from(row: ResultRow) = MemberRow(
            id          = row[MemberTable.id].value,
            provider    = row[MemberTable.provider],
            providerId  = row[MemberTable.providerId],
            email       = row[MemberTable.email],
            nickname    = row[MemberTable.nickname],
            isDelete    = row[MemberTable.isDelete],
            createdAt = row[MemberTable.createdAt],
            updatedAt = row[MemberTable.updatedAt],
        )
    }
}