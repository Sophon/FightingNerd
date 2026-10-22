package io.github.sophon.glossaryinfil.adapter.outbound.sqldelight

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.Result
import io.github.sophon.glossaryinfil.data.GlossaryDB
import io.github.sophon.glossaryinfil.model.GlossaryItem
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.Test

/**
 * Each test starts by deleting [DATABASE_PATH] and leaves it behind - open it in DataGrip
 * to see what the last test wrote.
 */
internal class SqlDelightGlossaryAdapterTest {
    @Test
    fun `replaced glossary is loaded by its term`() = runTest {
        // given
        val adapter = createAdapter()
        val expected = Result.Success(listOf(okizeme))

        // when
        adapter.replace(listOf(antiAir, okizeme))

        // then
        val result = adapter.load(query = "Okizeme", compactQuery = "Okizeme")
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `an item is loaded by its alt term`() = runTest {
        // given
        val adapter = createAdapter()
        val expected = Result.Success(listOf(antiAir))

        // when
        adapter.replace(listOf(antiAir, okizeme))

        // then
        val result = adapter.load(query = "AA", compactQuery = "AA")
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `a part of the term in any case loads the item`() = runTest {
        // given
        val adapter = createAdapter()
        val expected = Result.Success(listOf(whiffPunish))

        // when
        adapter.replace(listOf(antiAir, whiffPunish))

        // then
        val result = adapter.load(query = "WHIFF", compactQuery = "WHIFF")
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `compact query loads a term written without whitespace`() = runTest {
        // given
        val adapter = createAdapter()
        val expected = Result.Success(listOf(okizeme))

        // when
        adapter.replace(listOf(antiAir, okizeme))

        // then
        val result = adapter.load(query = "oki zeme", compactQuery = "okizeme")
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `an item matched by several aliases is loaded once`() = runTest {
        // given
        val adapter = createAdapter()
        val expected = Result.Success(listOf(whiffPunish))

        // when
        adapter.replace(listOf(whiffPunish))

        // then
        val result = adapter.load(query = "whiff punish", compactQuery = "whiffpunish")
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `items are loaded in glossary order`() = runTest {
        // given
        val adapter = createAdapter()
        val expected = listOf(whiffPunish, antiAir, okizeme)

        // when
        adapter.replace(expected)

        // then
        val result = adapter.load(query = "i", compactQuery = "i")
        assertThat(result).isEqualTo(Result.Success(expected))
    }

    @Test
    fun `replacing drops items absent from the new glossary`() = runTest {
        // given
        val adapter = createAdapter()
        val expected = Result.Success(emptyList<GlossaryItem>())

        // when
        adapter.replace(listOf(antiAir, okizeme))
        adapter.replace(listOf(whiffPunish))

        // then
        val result = adapter.load(query = "Okizeme", compactQuery = "Okizeme")
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `wildcards in the query are plain characters`() = runTest {
        // given
        val adapter = createAdapter()
        val expected = Result.Success(emptyList<GlossaryItem>())

        // when
        adapter.replace(listOf(antiAir, okizeme, whiffPunish))

        // then
        val percentResult = adapter.load(query = "%", compactQuery = "%")
        val underscoreResult = adapter.load(query = "_", compactQuery = "_")
        assertThat(percentResult).isEqualTo(expected)
        assertThat(underscoreResult).isEqualTo(expected)
    }

    @Test
    fun `count counts items, not their alt terms`() = runTest {
        // given
        val adapter = createAdapter()
        val expected = Result.Success(2L)

        // when
        adapter.replace(listOf(antiAir, okizeme))

        // then
        val result = adapter.count()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `an empty glossary counts zero`() = runTest {
        // given
        val adapter = createAdapter()
        val expected = Result.Success(0L)

        // when
        val result = adapter.count()

        // then
        assertThat(result).isEqualTo(expected)
    }


    private fun createAdapter(): SqlDelightGlossaryAdapter {
        val databaseFile = File(DATABASE_PATH)
        databaseFile.parentFile.mkdirs()
        databaseFile.delete()

        val driver = JdbcSqliteDriver(
            url = "jdbc:sqlite:${databaseFile.absolutePath}",
            schema = GlossaryDB.Schema,
        )
        val adapter = SqlDelightGlossaryAdapter(LazyGlossaryDB { driver })
        return adapter
    }
}


private val antiAir = GlossaryItem(
    term = "Anti-air",
    definition = "An attack that hits a jumping opponent",
    altTerm = listOf("AA"),
    games = listOf("SF", "TK"),
    url = GlossaryItem.Url(term = "https://glossary.infil.net/?t=Anti-air"),
)
private val okizeme = GlossaryItem(
    term = "Okizeme",
    definition = "Offense against a **__knockdown__** opponent as they wake up",
    altTerm = listOf("Oki"),
    games = listOf("SF", "GG", "TK"),
    jpTranslation = listOf("起き攻め (okizeme)", "Lit. wake-up attack"),
    url = GlossaryItem.Url(
        term = "https://glossary.infil.net/?t=Okizeme",
        video = "https://glossary.infil.net/videos/Okizeme.mp4",
        image = "https://glossary.infil.net/images/terms/Okizeme.png",
    ),
)
private val whiffPunish = GlossaryItem(
    term = "Whiff Punish",
    definition = "Hitting an opponent during the recovery of a missed attack",
    altTerm = listOf("Whiff Punishing"),
    url = GlossaryItem.Url(term = "https://glossary.infil.net/?t=Whiff%20Punish"),
)


// relative to the module - Gradle runs the tests from feat/glossaryInfil
private const val DATABASE_PATH = "build/test-db/glossary.db"
