package io.github.sophon.glossaryinfil.usecase

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.glossaryinfil.integration.data.GlossaryDB
import io.github.sophon.glossaryinfil.integration.model.GlossaryError
import io.github.sophon.glossaryinfil.integration.model.GlossaryItem
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class CacheGlossaryUseCaseTest {

    private lateinit var db: FakeGlossaryDB
    private lateinit var useCase: CacheGlossaryUseCase

    @BeforeTest
    fun setup() {
        db = FakeGlossaryDB()
        useCase = CacheGlossaryUseCase(db)
    }

    @Test
    fun `successfully caches single item with no alt terms`() = runTest {
        // Given
        val items = listOf(
            GlossaryItem(
                term = "Fireball",
                definition = "A projectile attack",
                altTerm = emptyList(),
                url = GlossaryItem.Url("https://glossary.infil.net/?t=Fireball")
            )
        )

        // When
        val result = useCase.invoke(items)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(db.insertedItems).hasSize(1)
        assertThat(db.insertedItems["Fireball"]).isEqualTo(items[0])
    }

    @Test
    fun `successfully caches multiple items without alt terms`() = runTest {
        // Given
        val items = listOf(
            GlossaryItem(
                term = "Fireball",
                definition = "A projectile",
                url = GlossaryItem.Url("https://glossary.infil.net/?t=Fireball")
            ),
            GlossaryItem(
                term = "Dragon Punch",
                definition = "An anti-air",
                url = GlossaryItem.Url("https://glossary.infil.net/?t=Dragon%20Punch"),
            ),
            GlossaryItem(
                term = "Combo",
                definition = "A sequence of attacks",
                url = GlossaryItem.Url("https://glossary.infil.net/?t=Combo")
            )
        )

        // When
        val result = useCase.invoke(items)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(db.insertedItems).hasSize(3)
        assertThat(db.insertedItems["Fireball"]).isEqualTo(items[0])
        assertThat(db.insertedItems["Dragon Punch"]).isEqualTo(items[1])
        assertThat(db.insertedItems["Combo"]).isEqualTo(items[2])
    }

    @Test
    fun `caches item with single alt term`() = runTest {
        // Given
        val item = GlossaryItem(
            term = "Throw",
            definition = "An unblockable close-range attack",
            altTerm = listOf("Grab"),
            url = GlossaryItem.Url("https://glossary.infil.net/?t=Throw")
        )

        // When
        val result = useCase.invoke(listOf(item))

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(db.insertedItems).hasSize(2)
        assertThat(db.insertedItems["Throw"]).isEqualTo(item)
        assertThat(db.insertedItems["Grab"]).isEqualTo(item)
    }

    @Test
    fun `caches item with multiple alt terms`() = runTest {
        // Given
        val item = GlossaryItem(
            term = "Super",
            definition = "A powerful special move",
            altTerm = listOf("Super Move", "Super Combo", "Ultimate"),
            url = GlossaryItem.Url("https://glossary.infil.net/?t=Super")
        )

        // When
        val result = useCase.invoke(listOf(item))

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(db.insertedItems).hasSize(4) // 1 main term + 3 alt terms
        assertThat(db.insertedItems["Super"]).isEqualTo(item)
        assertThat(db.insertedItems["Super Move"]).isEqualTo(item)
        assertThat(db.insertedItems["Super Combo"]).isEqualTo(item)
        assertThat(db.insertedItems["Ultimate"]).isEqualTo(item)
    }

    @Test
    fun `caches multiple items with alt terms`() = runTest {
        // Given
        val items = listOf(
            GlossaryItem(
                term = "Throw",
                definition = "Unblockable attack",
                altTerm = listOf("Grab"),
                url = GlossaryItem.Url("https://glossary.infil.net/?t=Throw"),
            ),
            GlossaryItem(
                term = "Block",
                definition = "Defending",
                altTerm = listOf("Guard", "Blocking"),
                url = GlossaryItem.Url("https://glossary.infil.net/?t=Block"),
            )
        )

        // When
        val result = useCase.invoke(items)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(db.insertedItems).hasSize(5) // Throw + Grab + Block + Guard + Blocking
        assertThat(db.insertedItems["Throw"]).isEqualTo(items[0])
        assertThat(db.insertedItems["Grab"]).isEqualTo(items[0])
        assertThat(db.insertedItems["Block"]).isEqualTo(items[1])
        assertThat(db.insertedItems["Guard"]).isEqualTo(items[1])
        assertThat(db.insertedItems["Blocking"]).isEqualTo(items[1])
    }

    @Test
    fun `successfully handles empty list`() = runTest {
        // Given
        val items = emptyList<GlossaryItem>()

        // When
        val result = useCase.invoke(items)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(db.insertedItems).hasSize(0)
    }

    @Test
    fun `propagates error from database on main term insertion`() = runTest {
        // Given
        val items = listOf(
            GlossaryItem(
                term = "Fireball",
                definition = "A projectile",
                url = GlossaryItem.Url("https://glossary.infil.net/?t=Fireball"),
            )
        )
        db.shouldFailOnInsert = true
        db.errorToReturn = GlossaryError.ERROR_DOWNLOADING_DATA

        // When
        val result = useCase.invoke(items)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isEqualTo(GlossaryError.ERROR_DOWNLOADING_DATA)
    }

    @Test
    fun `propagates error from database on alt term insertion`() = runTest {
        // Given
        val item = GlossaryItem(
            term = "Throw",
            definition = "Unblockable attack",
            altTerm = listOf("Grab"),
            url = GlossaryItem.Url("https://glossary.infil.net/?t=Throw"),
        )
        db.shouldFailOnInsert = true
        db.errorToReturn = GlossaryError.ERROR_DOWNLOADING_DATA

        // When
        val result = useCase.invoke(listOf(item))

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isEqualTo(GlossaryError.ERROR_DOWNLOADING_DATA)
    }

    @Test
    fun `same item can be found by main term and all alt terms`() = runTest {
        // Given
        val item = GlossaryItem(
            term = "Anti-Air",
            definition = "Hitting jumping opponents",
            altTerm = listOf("AA", "Anti Air"),
            url = GlossaryItem.Url("https://glossary.infil.net/?t=Anti-Air"),
        )

        // When
        useCase.invoke(listOf(item))

        // Then
        val mainTermItem = db.insertedItems["Anti-Air"]
        val altTerm1Item = db.insertedItems["AA"]
        val altTerm2Item = db.insertedItems["Anti Air"]

        assertThat(mainTermItem).isEqualTo(item)
        assertThat(altTerm1Item).isEqualTo(item)
        assertThat(altTerm2Item).isEqualTo(item)
        // All references should point to the same item
        assertThat(mainTermItem).isEqualTo(altTerm1Item)
        assertThat(altTerm1Item).isEqualTo(altTerm2Item)
    }

    // Fake implementation for testing
    private class FakeGlossaryDB : GlossaryDB {
        val insertedItems = mutableMapOf<String, GlossaryItem>()
        var shouldFailOnInsert = false
        var errorToReturn: GlossaryError = GlossaryError.ERROR_DOWNLOADING_DATA

        override suspend fun fetchDataFor(query: String): Result<List<GlossaryItem>, GlossaryError> {
            return Result.Success(emptyList())
        }

        override suspend fun insertData(term: String, item: GlossaryItem): EmptyResult<GlossaryError> {
            return if (shouldFailOnInsert) {
                Result.Error(errorToReturn)
            } else {
                insertedItems[term] = item
                Result.Success(Unit)
            }
        }
    }
}