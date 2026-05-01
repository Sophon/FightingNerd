package io.github.sophon.glossaryinfil.usecase

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.glossaryinfil.integration.GlossaryError
import io.github.sophon.glossaryinfil.data.GlossaryItemDto
import io.github.sophon.glossaryinfil.data.InfilGlossaryDataSource
import io.github.sophon.glossaryinfil.usecase.DownloadGlossaryUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class DownloadGlossaryUseCaseTest {

    private lateinit var dataSource: FakeInfilGlossaryDataSource
    private lateinit var useCase: DownloadGlossaryUseCase

    @BeforeTest
    fun setup() {
        dataSource = FakeInfilGlossaryDataSource()
        useCase = DownloadGlossaryUseCase(dataSource)
    }

    @Test
    fun `successfully downloads and maps single item with all fields populated`() = runTest {
        // Given
        val sourceItem = GlossaryItemDto(
            term = "Fireball",
            def = "A projectile that travels horizontally",
            altterm = listOf("Hadouken", "Projectile"),
            video = listOf("video1", "video2"),
            games = listOf("SF", "COM"),
            jp = "波動拳 (hadouken)<br>Lit. wave motion fist"
        )
        dataSource.setResult(Result.Success(listOf(sourceItem)))

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data).hasSize(1)

        val item = data[0]
        assertThat(item.term).isEqualTo("Fireball")
        assertThat(item.definition).isEqualTo("A projectile that travels horizontally")
        assertThat(item.altTerm).isEqualTo(listOf("Hadouken", "Projectile"))
        assertThat(item.url.video).isEqualTo("https://glossary.infil.net/videos/Fireball.mp4")
        assertThat(item.games).isEqualTo(listOf("SF", "COM"))
        assertThat(item.jpTranslation).isEqualTo(listOf("波動拳 (hadouken)", "Lit. wave motion fist"))
    }

    @Test
    fun `successfully downloads and maps item with null optional fields`() = runTest {
        // Given
        val sourceItem = GlossaryItemDto(
            term = "Combo",
            def = "A sequence of attacks",
            altterm = null,
            video = null,
            games = null,
            jp = null
        )
        dataSource.setResult(Result.Success(listOf(sourceItem)))

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data).hasSize(1)

        val item = data[0]
        assertThat(item.term).isEqualTo("Combo")
        assertThat(item.definition).isEqualTo("A sequence of attacks")
        assertThat(item.altTerm).isEmpty()
        assertThat(item.url.video).isNull()
        assertThat(item.games).isEmpty()
        assertThat(item.jpTranslation).isEmpty()
    }

    @Test
    fun `successfully downloads and maps item with empty lists`() = runTest {
        // Given
        val sourceItem = GlossaryItemDto(
            term = "Block",
            def = "Defending against attacks",
            altterm = emptyList(),
            video = emptyList(),
            games = emptyList(),
            jp = ""
        )
        dataSource.setResult(Result.Success(listOf(sourceItem)))

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data).hasSize(1)

        val item = data[0]
        assertThat(item.term).isEqualTo("Block")
        assertThat(item.altTerm).isEmpty()
        assertThat(item.url.video).isNull()
        assertThat(item.games).isEmpty()
        assertThat(item.jpTranslation).isEqualTo(listOf(""))
    }

    @Test
    fun `successfully downloads and maps multiple items`() = runTest {
        // Given
        val sourceItems = listOf(
            GlossaryItemDto(
                term = "Fireball",
                def = "A projectile",
                altterm = listOf("Hadouken"),
                video = null,
                games = null,
                jp = "波動拳"
            ),
            GlossaryItemDto(
                term = "Dragon Punch",
                def = "An anti-air",
                altterm = listOf("DP", "Shoryuken"),
                video = null,
                games = null,
                jp = "昇龍拳"
            ),
            GlossaryItemDto(
                term = "Throw",
                def = "Unblockable attack",
                altterm = listOf("Grab"),
                video = null,
                games = null,
                jp = null
            )
        )
        dataSource.setResult(Result.Success(sourceItems))

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data).hasSize(3)
        assertThat(data[0].term).isEqualTo("Fireball")
        assertThat(data[1].term).isEqualTo("Dragon Punch")
        assertThat(data[2].term).isEqualTo("Throw")
    }

    @Test
    fun `splits japanese translation by br tags`() = runTest {
        // Given
        val sourceItem = GlossaryItemDto(
            term = "Guard Crush",
            def = "Breaking an opponent's guard",
            altterm = null,
            video = null,
            games = null,
            jp = "ガードクラッシュ (gādo kurasshu)<br>Lit. guard crush<br>ガークラ (gā kura)"
        )
        dataSource.setResult(Result.Success(listOf(sourceItem)))

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        val item = data[0]
        assertThat(item.jpTranslation).isEqualTo(
            listOf(
                "ガードクラッシュ (gādo kurasshu)",
                "Lit. guard crush",
                "ガークラ (gā kura)"
            )
        )
    }

    @Test
    fun `handles single line japanese translation without br tags`() = runTest {
        // Given
        val sourceItem = GlossaryItemDto(
            term = "Jab",
            def = "Light punch",
            altterm = null,
            video = null,
            games = null,
            jp = "小パン (ko pan)"
        )
        dataSource.setResult(Result.Success(listOf(sourceItem)))

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        val item = data[0]
        assertThat(item.jpTranslation).isEqualTo(listOf("小パン (ko pan)"))
    }

    @Test
    fun `successfully handles empty list from data source`() = runTest {
        // Given
        dataSource.setResult(Result.Success(emptyList()))

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data).isEmpty()
    }

    @Test
    fun `maps data source error to glossary error`() = runTest {
        // Given
        dataSource.setResult(Result.Error(DataError.Remote.REQUEST_TIMEOUT))

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isEqualTo(GlossaryError.ERROR_DOWNLOADING_DATA)
    }

    @Test
    fun `maps any data source error to ERROR_DOWNLOADING_DATA`() = runTest {
        // Given
        dataSource.setResult(Result.Error(DataError.Remote.SERVER_ERROR))

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isEqualTo(GlossaryError.ERROR_DOWNLOADING_DATA)
    }

    @Test
    fun `preserves order of items from data source`() = runTest {
        // Given
        val sourceItems = listOf(
            GlossaryItemDto(term = "Zoning", def = "Long range control", null, null, null, null),
            GlossaryItemDto(term = "Anti-Air", def = "Hit jumping opponents", null, null, null, null),
            GlossaryItemDto(term = "Mixup", def = "Unpredictable offense", null, null, null, null)
        )
        dataSource.setResult(Result.Success(sourceItems))

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data).hasSize(3)
        assertThat(data[0].term).isEqualTo("Zoning")
        assertThat(data[1].term).isEqualTo("Anti-Air")
        assertThat(data[2].term).isEqualTo("Mixup")
    }

    // Fake implementation for testing
    private class FakeInfilGlossaryDataSource : InfilGlossaryDataSource {
        private var result: Result<List<GlossaryItemDto>, DataError.Remote>? = null

        fun setResult(result: Result<List<GlossaryItemDto>, DataError.Remote>) {
            this.result = result
        }

        override suspend fun getGlossary(): Result<List<GlossaryItemDto>, DataError.Remote> {
            return result ?: Result.Success(emptyList())
        }
    }
}