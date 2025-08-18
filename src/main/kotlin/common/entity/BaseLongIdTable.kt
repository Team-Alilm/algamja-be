package org.team_alilm.common.entity

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder

open class BaseLongIdTable(name: String) : LongIdTable(name) {
    val isDelete    = bool("is_delete").default(false)
    val createdDate = long("created_date")
    val updatedDate = long("last_modified_date")
}

/** 공통 insert: created/updated 밀리초 자동 세팅 */
fun BaseLongIdTable.insertAudited(
    block: BaseLongIdTable.(InsertStatement<Number>) -> Unit
): InsertStatement<Number> = insert {
    val now = System.currentTimeMillis()
    it[createdDate] = now
    it[updatedDate] = now
    this@insertAudited.block(it)
}

/** 공통 update: updated 밀리초 자동 갱신 */
fun BaseLongIdTable.updateAudited(
    where: SqlExpressionBuilder.() -> Op<Boolean>,
    block: BaseLongIdTable.(UpdateBuilder<*>) -> Unit
): Int = update(where) {
    it[updatedDate] = System.currentTimeMillis()
    this@updateAudited.block(it)
}

/** 소프트 삭제 공통 */
fun BaseLongIdTable.softDeleteById(idValue: Long): Int {
    val t = this // ← 바깥 테이블 캡처
    return update({ t.id eq EntityID(idValue, t) }) { // 타입 맞게 EntityID 사용
        it[isDelete] = true
        it[updatedDate] = System.currentTimeMillis()
    }
}