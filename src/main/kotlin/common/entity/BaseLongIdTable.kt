package org.team_alilm.common.entity

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder

open class BaseLongIdTable(name: String) : LongIdTable(name) {
    val isDelete    = bool("is_delete").default(false)
    val createdDate = long("created_date")
    val lastModifiedDate = long("last_modified_date")
}

/**
 * Inserts a new row in the table and automatically sets auditing fields.
 *
 * Sets `createdDate` and `lastModifiedDate` to the current epoch milliseconds and `isDelete` to `false`,
 * then invokes [block] to populate additional columns for the insert.
 *
 * @param block Extension block applied to the generated [InsertStatement] to set other column values.
 * @return The resulting [InsertStatement]<Number> from the insert operation.
 */
fun <T : BaseLongIdTable> T.insertAudited(
    block: T.(InsertStatement<Number>) -> Unit
): InsertStatement<Number> = insert {
    val now = System.currentTimeMillis()
    it[createdDate] = now
    it[lastModifiedDate] = now
    it[isDelete] = false
    this@insertAudited.block(it)
}

/** 공통 update: updated 밀리초 자동 갱신 */
fun <T : BaseLongIdTable> T.updateAudited(
    where: SqlExpressionBuilder.() -> Op<Boolean>,
    block: T.(UpdateBuilder<*>) -> Unit
): Int = update(where) {
    it[lastModifiedDate] = System.currentTimeMillis()
    this@updateAudited.block(it)
}

/** 공통 소프트 삭제: Long PK로 비교 (EntityID 직접 생성 X) */
fun <T : BaseLongIdTable> T.softDeleteById(idValue: Long): Int =
    update({ this@softDeleteById.id eq idValue }) {   // ← 테이블을 명시
        it[isDelete] = true
        it[lastModifiedDate] = System.currentTimeMillis()
    }