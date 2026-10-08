package io.github.sophon.glossaryinfil.adapter.outbound.sqldelight

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.sqldelight.fingerprint
import io.github.sophon.core.sqldelight.readStoredFingerprint
import io.github.sophon.core.sqldelight.storeFingerprint
import io.github.sophon.glossaryinfil.data.GlossaryDB
import io.github.sophon.glossaryinfil.model.GlossaryItem
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test

/**
 * Each test starts by deleting [DATABASE_PATH] and leaves it behind - open it in DataGrip
 * to see what the last test wrote.
 */
internal class GlossarySqlDriverTest {
    private val databaseFile = File(DATABASE_PATH)

    @BeforeTest
    fun setup() {
        databaseFile.parentFile.mkdirs()
        databaseFile.delete()
    }

    @Test
    fun `new database stores the current fingerprint`() {
        // given
        val expected = GlossaryDB.Schema.fingerprint()

        // when
        val driver = openFingerprintedDriver(open = ::openDriver, delete = ::deleteDatabase)

        // then
        val result = driver.readStoredFingerprint()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `database with the current fingerprint keeps its data`() = runTest {
        // given
        val expected = Result.Success(1L)
        val currentDriver = openFingerprintedDriver(open = ::openDriver, delete = ::deleteDatabase)
        createAdapter(currentDriver).replace(listOf(okizeme))
        currentDriver.close()

        // when
        val driver = openFingerprintedDriver(open = ::openDriver, delete = ::deleteDatabase)

        // then
        val result = createAdapter(driver).count()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `changed fingerprint drops the stored data`() = runTest {
        // given
        val expected = Result.Success(0L)
        storeOutdatedGlossary()

        // when
        val driver = openFingerprintedDriver(open = ::openDriver, delete = ::deleteDatabase)

        // then
        val result = createAdapter(driver).count()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `recreated database stores the current fingerprint`() = runTest {
        // given
        val expected = GlossaryDB.Schema.fingerprint()
        storeOutdatedGlossary()

        // when
        val driver = openFingerprintedDriver(open = ::openDriver, delete = ::deleteDatabase)

        // then
        val result = driver.readStoredFingerprint()
        assertThat(result).isEqualTo(expected)
    }


    private fun openDriver(): SqlDriver {
        val driver = JdbcSqliteDriver(
            url = "jdbc:sqlite:${databaseFile.absolutePath}",
            schema = GlossaryDB.Schema,
        )
        return driver
    }

    private fun deleteDatabase() {
        databaseFile.delete()
    }

    private fun createAdapter(driver: SqlDriver): SqlDelightGlossaryAdapter {
        val adapter = SqlDelightGlossaryAdapter(LazyGlossaryDB { driver })
        return adapter
    }

    // a stored glossary written by an older schema
    private suspend fun storeOutdatedGlossary() {
        val outdatedDriver = openDriver()
        outdatedDriver.storeFingerprint(OUTDATED_FINGERPRINT)
        createAdapter(outdatedDriver).replace(listOf(okizeme))
        outdatedDriver.close()
    }
}


private val okizeme = GlossaryItem(
    term = "Okizeme",
    definition = "Offense against a **__knockdown__** opponent as they wake up",
    altTerm = listOf("Oki"),
    url = GlossaryItem.Url(term = "https://glossary.infil.net/?t=Okizeme"),
)


// relative to the module - Gradle runs the tests from feat/glossaryInfil
private const val DATABASE_PATH = "build/test-db/glossary-driver.db"
private const val OUTDATED_FINGERPRINT = "5f3e2a91"
