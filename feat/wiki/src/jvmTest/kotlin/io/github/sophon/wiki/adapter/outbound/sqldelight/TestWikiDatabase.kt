package io.github.sophon.wiki.adapter.outbound.sqldelight

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.DragDownSqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.DreamCancelSqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.DustLoopSqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.MizuumiSqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.SuperComboSqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.WavuSqlDelightGameProperties
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.data.WikiDB
import java.io.File
import java.util.Properties
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * A fresh [WikiDB] file with foreign keys on, like the production driver, and both adapters over it.
 * Each test starts by deleting the file and leaves it behind - open [DATABASE_PATH] in DataGrip
 * to see what the last test wrote.
 */
internal class TestWikiDatabase(clock: Clock = FakeClock()) {
    private val driver = run {
        val databaseFile = File(DATABASE_PATH)
        databaseFile.parentFile.mkdirs()
        databaseFile.delete()

        JdbcSqliteDriver(
            url = "jdbc:sqlite:${databaseFile.absolutePath}",
            properties = Properties().apply { put("foreign_keys", "true") },
            schema = WikiDB.Schema,
        )
    }
    private val wikiDatabase = LazyWikiDB { driver }
    private val gamePropertiesRouter = SqlDelightGamePropertiesRouter(
        wavuGameProperties = WavuSqlDelightGameProperties(wikiDatabase),
        mizuumiGameProperties = MizuumiSqlDelightGameProperties(wikiDatabase),
        dustLoopGameProperties = DustLoopSqlDelightGameProperties(wikiDatabase),
        superComboGameProperties = SuperComboSqlDelightGameProperties(wikiDatabase),
        dragDownGameProperties = DragDownSqlDelightGameProperties(wikiDatabase),
        dreamCancelGameProperties = DreamCancelSqlDelightGameProperties(wikiDatabase),
    )

    val characterAdapter = SqlDelightCharacterAdapter(wikiDatabase, gamePropertiesRouter, clock)
    val moveAdapter = SqlDelightMoveAdapter(wikiDatabase, gamePropertiesRouter)

    suspend fun save(
        character: Character,
        moveList: List<Move> = emptyList(),
    ) {
        val result = characterAdapter.save(character, moveList)
        assertThat(result).isEqualTo(Result.Success(Unit))
    }

    fun countRows(table: String): Long {
        val count = driver.executeQuery(
            identifier = null,
            sql = "SELECT COUNT(*) FROM $table",
            mapper = { cursor ->
                cursor.next()
                QueryResult.Value(cursor.getLong(0) ?: 0L)
            },
            parameters = 0,
        ).value
        return count
    }
}

internal class FakeClock(var now: Instant = Instant.fromEpochMilliseconds(1_759_104_000_000)) : Clock {
    override fun now(): Instant = now
}


// relative to the module - Gradle runs the tests from feat/wiki
private const val DATABASE_PATH = "build/test-db/wiki.db"
