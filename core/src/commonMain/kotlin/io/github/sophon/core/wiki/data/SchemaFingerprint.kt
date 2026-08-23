package io.github.sophon.core.wiki.data

import app.cash.sqldelight.Query
import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement
import app.cash.sqldelight.db.SqlSchema

private const val META_TABLE = "__schema_meta"
private const val CREATE_META =
    "CREATE TABLE IF NOT EXISTS $META_TABLE (id INTEGER PRIMARY KEY, hash TEXT NOT NULL)"

// Runs schema.create() against a capturing stub driver to collect every CREATE statement,
// then hashes them. Any .sq edit changes the emitted SQL and therefore the hash.
fun SqlSchema<QueryResult.Value<Unit>>.fingerprint(): String {
    val capturing = HashCapturingDriver()
    create(capturing)
    val concatenated = capturing.statements.joinToString("\n")
    val hash = concatenated.hashCode().toLong() and 0xFFFFFFFFL
    val result = hash.toString(16)
    return result
}

fun SqlDriver.readStoredFingerprint(): String? {
    execute(null, CREATE_META, 0)
    val queryResult = executeQuery(
        identifier = null,
        sql = "SELECT hash FROM $META_TABLE WHERE id = 1",
        mapper = { cursor ->
            val stored = if (cursor.next().value) cursor.getString(0) else null
            QueryResult.Value(stored)
        },
        parameters = 0,
    )
    val stored = queryResult.value
    return stored
}

fun SqlDriver.storeFingerprint(hash: String) {
    execute(null, CREATE_META, 0)
    execute(
        identifier = null,
        sql = "INSERT OR REPLACE INTO $META_TABLE (id, hash) VALUES (1, ?)",
        parameters = 1,
        binders = { bindString(0, hash) },
    )
}

private class HashCapturingDriver : SqlDriver {
    val statements = mutableListOf<String>()

    override fun execute(
        identifier: Int?,
        sql: String,
        parameters: Int,
        binders: (SqlPreparedStatement.() -> Unit)?,
    ): QueryResult<Long> {
        statements.add(sql)
        return QueryResult.Value(0L)
    }

    override fun <R> executeQuery(
        identifier: Int?,
        sql: String,
        mapper: (SqlCursor) -> QueryResult<R>,
        parameters: Int,
        binders: (SqlPreparedStatement.() -> Unit)?,
    ): QueryResult<R> = throw UnsupportedOperationException()

    override fun addListener(vararg queryKeys: String, listener: Query.Listener) = Unit
    override fun removeListener(vararg queryKeys: String, listener: Query.Listener) = Unit
    override fun notifyListeners(vararg queryKeys: String) = Unit
    override fun close() = Unit
    override fun currentTransaction(): Transacter.Transaction? = null
    override fun newTransaction(): QueryResult<Transacter.Transaction> =
        throw UnsupportedOperationException()
}
