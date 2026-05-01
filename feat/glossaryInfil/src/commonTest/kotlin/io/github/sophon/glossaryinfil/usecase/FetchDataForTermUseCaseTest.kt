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

class FetchDataForTermUseCaseTest {
    private lateinit var db: FakeGlossaryDB
    private lateinit var useCase: FetchDataForTermUseCase

    @BeforeTest
    fun setup() {
        db = FakeGlossaryDB()
        useCase = FetchDataForTermUseCase(db)
    }

    @Test
    fun `exact match case insensitive comes first`() = runTest {
        // Given
        val query = "Fireball"
        val items = listOf(
            GlossaryItem(
                term = "Fireball Motion",
                definition = "A quarter circle forward motion",
                url = GlossaryItem.Url("https://glossary.infil.net/?t=Fireball"),
            ),
            GlossaryItem(
                term = "fireball",
                definition = "A projectile that travels horizontally",
                url = GlossaryItem.Url("https://glossary.infil.net/?t=Fireball"),
            ),
            GlossaryItem(
                term = "Fireball Character",
                definition = "A character with strong fireballs",
                url = GlossaryItem.Url("https://glossary.infil.net/?t=Fireball"),
            )
        )
        db.setResult(Result.Success(items))

        // When
        val result = useCase.invoke(query)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data[0].term).isEqualTo("fireball")
    }

    @Test
    fun `exact match without whitespace comes second`() = runTest {
        // Given
        val query = "sonic boom"
        val items = listOf(
            GlossaryItem(
                term = "Sonic Boom Character",
                definition = "Characters with charge moves",
                url = GlossaryItem.Url("https://glossary.infil.net/?t=Sonic%20Boom"),
            ),
            GlossaryItem(
                term = "sonicboom",
                definition = "A charge projectile",
                url = GlossaryItem.Url("https://glossary.infil.net/?t=Sonic%20Boom"),
            ),
            GlossaryItem(
                term = "Sonic",
                definition = "Fast character",
                url = GlossaryItem.Url("https://glossary.infil.net/?t=Sonic%20Boom"),
            )
        )
        db.setResult(Result.Success(items))

        // When
        val result = useCase.invoke(query)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data[0].term).isEqualTo("sonicboom")
    }

    @Test
    fun `partial matches sorted by length ascending`() = runTest {
        // Given
        val query = "dragon"
        val items = listOf(
            GlossaryItem(
                term = "Dragon Punch Motion",
                definition = "Forward, down, down-forward motion",
                url = GlossaryItem.Url(""),
            ),
            GlossaryItem(
                term = "Dragon",
                definition = "A type of move",
                url = GlossaryItem.Url(""),
            ),
            GlossaryItem(
                term = "Dragon Punch",
                definition = "An invincible anti-air",
                url = GlossaryItem.Url(""),
            ),
            GlossaryItem(
                term = "Dragon Ball FighterZ Mechanic",
                definition = "Game specific mechanic",
                url = GlossaryItem.Url(""),
            )
        )
        db.setResult(Result.Success(items))

        // When
        val result = useCase.invoke(query)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data[0].term).isEqualTo("Dragon") // Length 6
        assertThat(data[1].term).isEqualTo("Dragon Punch") // Length 12
        assertThat(data[2].term).isEqualTo("Dragon Punch Motion") // Length 19
        assertThat(data[3].term).isEqualTo("Dragon Ball FighterZ Mechanic") // Length 30
    }

    @Test
    fun `complete sorting hierarchy works correctly`() = runTest {
        // Given
        val query = "Block"
        val items = listOf(
            GlossaryItem(
                term = "Block String",
                definition = "A sequence of blocked attacks",
                url = GlossaryItem.Url(""),
            ),
            GlossaryItem(
                term = "Blocking",
                definition = "The act of defending",
                url = GlossaryItem.Url(""),
            ),
            GlossaryItem(
                term = "block",
                definition = "Defending against attacks",
                url = GlossaryItem.Url(""),
            ),
            GlossaryItem(
                term = "B L O C K",
                definition = "Spaced out term",
                url = GlossaryItem.Url(""),
            ),
            GlossaryItem(
                term = "Block Stun",
                definition = "Period after blocking",
                url = GlossaryItem.Url(""),
            ),
            GlossaryItem(
                term = "Cross-up Block Direction",
                definition = "Which way to block",
                url = GlossaryItem.Url(""),
            )
        )
        db.setResult(Result.Success(items))

        // When
        val result = useCase.invoke(query)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data).hasSize(6)
        assertThat(data[0].term).isEqualTo("block")
        assertThat(data[1].term).isEqualTo("B L O C K")
        assertThat(data[2].term).isEqualTo("Blocking")
        assertThat(data[3].term).isEqualTo("Block Stun")
        assertThat(data[4].term).isEqualTo("Block String")
        assertThat(data[5].term).isEqualTo("Cross-up Block Direction")
    }

    @Test
    fun `duplicates are removed by term`() = runTest {
        // Given
        val query = "throw"
        val items = listOf(
            GlossaryItem(
                term = "Throw",
                definition = "A close-range unblockable attack",
                url = GlossaryItem.Url(""),
            ),
            GlossaryItem(
                term = "Throw",
                definition = "Different definition for same term",
                url = GlossaryItem.Url(""),
            ),
            GlossaryItem(
                term = "Throw Tech",
                definition = "Defending against throws",
                url = GlossaryItem.Url(""),
            )
        )
        db.setResult(Result.Success(items))

        // When
        val result = useCase.invoke(query)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data).hasSize(2)
        assertThat(data[0].term).isEqualTo("Throw")
        assertThat(data[1].term).isEqualTo("Throw Tech")
    }

    @Test
    fun `exact match first followed by partial matches`() = runTest {
        // Given
        val query = "frame data"
        val items = listOf(
            GlossaryItem(
                term = "framedata",
                definition = "Numerical properties of moves",
                url = GlossaryItem.Url(""),
            ),
            GlossaryItem(
                term = "Frame Data",
                definition = "Information about move timing",
                url = GlossaryItem.Url(""),
            )
        )
        db.setResult(Result.Success(items))

        // When
        val result = useCase.invoke(query)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        // "Frame Data" is exact match (case insensitive), should come first
        assertThat(data[0].term).isEqualTo("Frame Data")
        // "framedata" is exact match without whitespace, should come second
        assertThat(data[1].term).isEqualTo("framedata")
    }

    @Test
    fun `returns empty list when db returns empty list`() = runTest {
        // Given
        val query = "nonexistentterm"
        db.setResult(Result.Success(emptyList()))

        // When
        val result = useCase.invoke(query)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data).hasSize(0)
    }

    @Test
    fun `propagates error from database`() = runTest {
        // Given
        val query = "combo"
        db.setResult(Result.Error(GlossaryError.ERROR_DOWNLOADING_DATA))

        // When
        val result = useCase.invoke(query)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isEqualTo(GlossaryError.ERROR_DOWNLOADING_DATA)
    }

    @Test
    fun `case insensitivity works for various cases`() = runTest {
        // Given
        val query = "SUPER"
        val items = listOf(
            GlossaryItem(
                term = "super",
                definition = "A powerful special move",
                url = GlossaryItem.Url(""),
            ),
            GlossaryItem(
                term = "Super",
                definition = "Same as above",
                url = GlossaryItem.Url(""),
            ),
            GlossaryItem(
                term = "SUPER",
                definition = "All caps version",
                url = GlossaryItem.Url(""),
            ),
            GlossaryItem(
                term = "SuPeR",
                definition = "Mixed case version",
                url = GlossaryItem.Url(""),
            )
        )
        db.setResult(Result.Success(items))

        // When
        val result = useCase.invoke(query)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        // All should be treated as exact matches
        assertThat(data).hasSize(4)
    }

    @Test
    fun `search for common fighting game term returns sorted results`() = runTest {
        // Given
        val query = "combo"
        val items = listOf(
            GlossaryItem(
                term = "Combo Breaker",
                definition = "Breaking out of combos",
                url = GlossaryItem.Url(""),
            ),
            GlossaryItem(
                term = "Combo",
                definition = "A sequence of attacks",
                url = GlossaryItem.Url(""),
            ),
            GlossaryItem(
                term = "Air Combo",
                definition = "Combos performed in the air",
                url = GlossaryItem.Url(""),
            ),
            GlossaryItem(
                term = "Infinite Combo",
                definition = "A combo that loops forever",
                url = GlossaryItem.Url(""),
            )
        )
        db.setResult(Result.Success(items))

        // When
        val result = useCase.invoke(query)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data[0].term).isEqualTo("Combo") // Exact match first
        assertThat(data[1].term).isEqualTo("Air Combo") // Shorter partial match
        assertThat(data[2].term).isEqualTo("Combo Breaker") // Longer partial match
        assertThat(data[3].term).isEqualTo("Infinite Combo") // Longest partial match
    }

    @Test
    fun `multiple word query with exact match`() = runTest {
        // Given
        val query = "hit confirm"
        val items = listOf(
            GlossaryItem(
                term = "Hit Confirm Window",
                definition = "Time to confirm hits",
                url = GlossaryItem.Url(""),
            ),
            GlossaryItem(
                term = "Hit Confirm",
                definition = "Confirming an attack hit",
                url = GlossaryItem.Url(""),
            ),
            GlossaryItem(
                term = "Confirm",
                definition = "General confirmation",
                url = GlossaryItem.Url(""),
            )
        )
        db.setResult(Result.Success(items))

        // When
        val result = useCase.invoke(query)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data[0].term).isEqualTo("Hit Confirm")
    }

    @Test
    fun `handles empty query gracefully`() = runTest {
        // Given
        val query = ""
        db.setResult(Result.Success(emptyList()))

        // When
        val result = useCase.invoke(query)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data).hasSize(0)
    }

    @Test
    fun `handles query with only whitespace`() = runTest {
        // Given
        val query = "   "
        db.setResult(Result.Success(emptyList()))

        // When
        val result = useCase.invoke(query)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data).hasSize(0)
    }

    @Test
    fun `propagates empty glossary error from database`() = runTest {
        // Given
        val query = "combo"
        db.setResult(Result.Error(GlossaryError.EMPTY_GLOSSARY))

        // When
        val result = useCase.invoke(query)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isEqualTo(GlossaryError.EMPTY_GLOSSARY)
    }


    // Fake implementation for testing
    private class FakeGlossaryDB : GlossaryDB {
        private var result: Result<List<GlossaryItem>, GlossaryError>? = null

        fun setResult(result: Result<List<GlossaryItem>, GlossaryError>) {
            this.result = result
        }

        override suspend fun fetchDataFor(query: String): Result<List<GlossaryItem>, GlossaryError> {
            return result ?: Result.Success(emptyList())
        }

        override suspend fun insertData(term: String, item: GlossaryItem): EmptyResult<GlossaryError> {
            return Result.Success(Unit)
        }
    }
}