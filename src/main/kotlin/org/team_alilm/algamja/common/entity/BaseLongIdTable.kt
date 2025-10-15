package org.team_alilm.algamja.common.entity

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder

open class BaseLongIdTable(name: String) : LongIdTable(name) {
    val isDelete = bool("is_delete").default(false)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
}

/** 공통 insert: created/updated 밀리초 자동 세팅 */
fun <T : BaseLongIdTable> T.insertAudited(
    block: T.(InsertStatement<Number>) -> Unit
): InsertStatement<Number> = insert {
    val now = System.currentTimeMillis()
    it[createdAt] = now
    it[updatedAt] = now
    it[isDelete] = false
    this@insertAudited.block(it)
}

/** 공통 update: updated 밀리초 자동 갱신 */
fun <T : BaseLongIdTable> T.updateAudited(
    where: SqlExpressionBuilder.() -> Op<Boolean>,
    block: T.(UpdateBuilder<*>) -> Unit
): Int = update(where) {
    it[updatedAt] = System.currentTimeMillis()
    this@updateAudited.block(it)
}

/** 공통 소프트 삭제: Long PK로 비교 (EntityID 직접 생성 X) */
fun <T : BaseLongIdTable> T.softDeleteById(idValue: Long): Int =
    update({ this@softDeleteById.id eq idValue }) {   // ← 테이블을 명시
        it[isDelete] = true
        it[updatedAt] = System.currentTimeMillis()
    }