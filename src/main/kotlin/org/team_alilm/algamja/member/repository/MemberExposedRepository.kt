package org.team_alilm.algamja.member.repository

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.team_alilm.algamja.common.entity.insertAudited
import org.team_alilm.algamja.common.entity.softDeleteById
import org.team_alilm.algamja.common.entity.updateAudited
import org.team_alilm.algamja.common.enums.Provider
import org.team_alilm.algamja.member.entity.MemberTable
import org.team_alilm.algamja.member.entity.MemberRow

@Repository
class MemberExposedRepository {

    /** 단건 조회 (id) */
    fun fetchById(id: Long): MemberRow? =
        MemberTable
            .selectAll()
            .where { (MemberTable.id eq id) and (MemberTable.isDelete eq false) }
            .map { MemberRow.from(it) }
            .firstOrNull()

    /** 단건 조회 (provider + providerId) */
    fun fetchByProviderAndProviderId(
        provider: Provider,
        providerId: String
    ): MemberRow? =
        MemberTable
            .selectAll()
            .where {
                (MemberTable.provider eq provider) and
                        (MemberTable.providerId eq providerId) and
                        (MemberTable.isDelete eq false)
            }
            .limit(1)
            .firstOrNull()
            ?.let(MemberRow::from)

    /** 페이지 조회 */
    fun fetchPage(limit: Int, offset: Long): List<MemberRow> =
        MemberTable
            .selectAll()
            .where { MemberTable.isDelete eq false }
            .orderBy(MemberTable.id to SortOrder.DESC)
            .limit(count = limit).offset(start = offset)
            .map(MemberRow::from)

    /** 존재 여부 */
    fun existsByProviderAndProviderId(
        provider: Provider,
        providerId: String
    ): Boolean =
        MemberTable
            .selectAll()
            .where {
                (MemberTable.provider eq provider) and
                        (MemberTable.providerId eq providerId) and
                        (MemberTable.isDelete eq false)
            }
            .limit(1)
            .any()              // 하나라도 있으면 true

    /** 활성 회원 수 */
    fun countActive(): Long =
        MemberTable
            .selectAll()
            .where { MemberTable.isDelete eq false }
            .count()

    /** 생성 (감사 컬럼 자동 세팅) */
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

    /** 닉네임 변경 (감사 updated 자동) */
    fun updateNickname(id: Long, nickname: String): Int =
        MemberTable.updateAudited(
            where = { MemberTable.id eq id }
        ) {
            it[this.nickname] = nickname
        }

    /** 소프트 삭제 */
    fun softDelete(id: Long): Int =
        MemberTable.softDeleteById(id)

    fun updateMember(nickname: String, email: String, memberId: Long): Int =
        MemberTable.updateAudited(
            where = { MemberTable.id eq memberId }
        ) {
            it[this.nickname] = nickname
            it[this.email] = email
        }

    /*
     * (옵션) MySQL 전용 Upsert가 필요하다면 아래 주석을 참고하세요.
     * 공식 문서에선 일반 DML 패턴을 권장하고, Upsert는 DB 방언 의존이어야 합니다.
     * 필요 시 Flyway로 'INSERT ... ON DUPLICATE KEY UPDATE'를 저장 프로시저/뷰로 감싸거나
     * Exposed의 onDuplicateKeyUpdate 확장(있는 버전만)을 사용하세요.
     */
    // fun upsertByProviderAndProviderId(...): Long { ... }
}