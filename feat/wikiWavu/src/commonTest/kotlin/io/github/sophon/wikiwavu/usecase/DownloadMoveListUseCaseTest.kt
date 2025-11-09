package io.github.sophon.wikiwavu.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.wikiwavu.data.CharacterListResponseDto
import io.github.sophon.wikiwavu.data.MoveDto
import io.github.sophon.wikiwavu.data.MoveListResponseDto
import io.github.sophon.wikiwavu.data.WavuWikiDataSource
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class DownloadMoveListUseCaseTest {
    //region Setup
    private lateinit var fakeDataSource: FakeWavuWikiDataSource
    private lateinit var useCase: DownloadMoveListUseCase

    @BeforeTest
    fun setup() {
        fakeDataSource = FakeWavuWikiDataSource()
        useCase = DownloadMoveListUseCase(fakeDataSource)
    }
    //endregion

    //region Success Cases
    @Test
    fun `invoke with character name returns success with mapped moves`() = runTest {
        // Given
        val charName = "Yoshimitsu"
        val dto = createSampleMoveListResponseDto()
        fakeDataSource.moveListResult = Result.Success(dto)

        // When
        val result = useCase.invoke(charName)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val moves = (result as Result.Success).data
        assertThat(moves.size).isEqualTo(3)

        // Verify first move mapping
        val firstMove = moves[0]
        assertThat(firstMove.charName).isEqualTo(charName)
        assertThat(firstMove.id).isEqualTo("yoshimitsu-1")
        assertThat(firstMove.input).isEqualTo("1")
        assertThat(firstMove.name).isEqualTo("Jab")
        assertThat(firstMove.damage).isEqualTo("5")
        assertThat(firstMove.startup).isEqualTo("i10")
        assertThat(firstMove.onBlock).isEqualTo("+1")
        assertThat(firstMove.onHit).isEqualTo("+8")
    }

    @Test
    fun `invoke passes character name to data source`() = runTest {
        // Given
        val charName = "Paul"
        fakeDataSource.moveListResult = Result.Success(createEmptyMoveListResponseDto())

        // When
        useCase.invoke(charName)

        // Then
        assertThat(fakeDataSource.lastCharNameQueried).isEqualTo(charName)
    }

    @Test
    fun `invoke with parent moves creates proper hierarchy`() = runTest {
        // Given
        val charName = "Yoshimitsu"
        val dto = createMoveListWithParentRelationship()
        fakeDataSource.moveListResult = Result.Success(dto)

        // When
        val result = useCase.invoke(charName)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val moves = (result as Result.Success).data

        // Verify parent move
        val parentMove = moves.find { it.id == "yoshimitsu-1" }
        assertThat(parentMove?.input).isEqualTo("1")

        // Verify child move
        val childMove = moves.find { it.id == "yoshimitsu-11" }
        assertThat(childMove?.input).isEqualTo("11")
        assertThat(childMove?.name).isEqualTo("Naguri Kabuto Wari")
    }

    @Test
    fun `invoke with heat moves maps heat properties correctly`() = runTest {
        // Given
        val charName = "Yoshimitsu"
        val dto = createMoveListWithHeatMove()
        fakeDataSource.moveListResult = Result.Success(dto)

        // When
        val result = useCase.invoke(charName)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val moves = (result as Result.Success).data
        val heatMove = moves.find { it.id == "yoshimitsu-31" }
        assertThat(heatMove?.t8Properties?.isHeat).isEqualTo(true)
    }

    @Test
    fun `invoke with homing moves maps homing properties correctly`() = runTest {
        // Given
        val charName = "Yoshimitsu"
        val dto = createMoveListWithHomingMove()
        fakeDataSource.moveListResult = Result.Success(dto)

        // When
        val result = useCase.invoke(charName)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val moves = (result as Result.Success).data
        val homingMove = moves.find { it.id == "yoshimitsu-1+3" }
        assertThat(homingMove?.t8Properties?.isHoming).isEqualTo(true)
    }
    //endregion

    //region Error Cases
    @Test
    fun `invoke with request timeout returns mapped wiki error`() = runTest {
        // Given
        val charName = "Yoshimitsu"
        fakeDataSource.moveListResult = Result.Error(DataError.Remote.REQUEST_TIMEOUT)

        // When
        val result = useCase.invoke(charName)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(WikiError::class)
    }

    @Test
    fun `invoke with server error returns mapped wiki error`() = runTest {
        // Given
        val charName = "Yoshimitsu"
        fakeDataSource.moveListResult = Result.Error(DataError.Remote.SERVER_ERROR)

        // When
        val result = useCase.invoke(charName)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(WikiError::class)
    }

    @Test
    fun `invoke with no internet returns mapped wiki error`() = runTest {
        // Given
        val charName = "Yoshimitsu"
        fakeDataSource.moveListResult = Result.Error(DataError.Remote.NO_INTERNET)

        // When
        val result = useCase.invoke(charName)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(WikiError::class)
    }

    @Test
    fun `invoke with serialization error returns mapped wiki error`() = runTest {
        // Given
        val charName = "Yoshimitsu"
        fakeDataSource.moveListResult = Result.Error(DataError.Remote.SERVER_ERROR)

        // When
        val result = useCase.invoke(charName)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(WikiError::class)
    }

    @Test
    fun `invoke with unknown error returns mapped wiki error`() = runTest {
        // Given
        val charName = "Yoshimitsu"
        fakeDataSource.moveListResult = Result.Error(DataError.Remote.UNKNOWN)

        // When
        val result = useCase.invoke(charName)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(WikiError::class)
    }
    //endregion

    //region Test Data Factories
    private fun createSampleMoveListResponseDto() = MoveListResponseDto(
        cargoQuery = listOf(
            MoveListResponseDto.Title(
                title = MoveDto(
                    id = "Yoshimitsu-1",
                    name = "Jab",
                    input = "1",
                    parent = null,
                    target = "h",
                    damage = "5",
                    startup = "i10",
                    recv = "r19",
                    tot = "29",
                    crush = null,
                    block = "+1",
                    hit = "+8",
                    ch = null,
                    notes = "Recovers 2f faster on hit or block (t27 r17)",
                    alias = null,
                    image = null,
                    video = null,
                    alt = null
                )
            ),
            MoveListResponseDto.Title(
                title = MoveDto(
                    id = "Yoshimitsu-2",
                    name = "Right Jab",
                    input = "2",
                    parent = null,
                    target = "h",
                    damage = "10",
                    startup = "i11",
                    recv = "r20",
                    tot = "31",
                    crush = null,
                    block = "-1",
                    hit = "+5",
                    ch = "+8",
                    notes = null,
                    alias = null,
                    image = null,
                    video = null,
                    alt = null
                )
            ),
            MoveListResponseDto.Title(
                title = MoveDto(
                    id = "Yoshimitsu-3",
                    name = "Enma",
                    input = "3",
                    parent = null,
                    target = "m",
                    damage = "12",
                    startup = "i15~16",
                    recv = "r29",
                    tot = "45",
                    crush = null,
                    block = "-9",
                    hit = "+5",
                    ch = null,
                    notes = null,
                    alias = null,
                    image = null,
                    video = null,
                    alt = null
                )
            )
        )
    )

    private fun createEmptyMoveListResponseDto() = MoveListResponseDto(
        cargoQuery = emptyList()
    )

    private fun createMoveListWithParentRelationship() = MoveListResponseDto(
        cargoQuery = listOf(
            MoveListResponseDto.Title(
                title = MoveDto(
                    id = "Yoshimitsu-1",
                    name = "Jab",
                    input = "1",
                    parent = null,
                    target = "h",
                    damage = "5",
                    startup = "i10",
                    recv = "r19",
                    tot = "29",
                    crush = null,
                    block = "+1",
                    hit = "+8",
                    ch = null,
                    notes = null,
                    alias = null,
                    image = null,
                    video = null,
                    alt = null
                )
            ),
            MoveListResponseDto.Title(
                title = MoveDto(
                    id = "Yoshimitsu-1,1",
                    name = "Naguri Kabuto Wari",
                    input = ",1",
                    parent = "Yoshimitsu-1",
                    target = ",m",
                    damage = ",19",
                    startup = ",i23",
                    recv = "r34 1SS",
                    tot = "67",
                    crush = null,
                    block = "-9",
                    hit = "+4c",
                    ch = "+6a",
                    notes = "&lt;div class=&quot;plainlist&quot;&gt;\n* Floor Break\n* Weapon\n&lt;/div&gt;",
                    alias = null,
                    image = null,
                    video = "File:t8-p2-yoshimitsu-1,1.mp4",
                    alt = null
                )
            )
        )
    )

    private fun createMoveListWithHeatMove() = MoveListResponseDto(
        cargoQuery = listOf(
            MoveListResponseDto.Title(
                title = MoveDto(
                    id = "Yoshimitsu-3,1",
                    name = "Enma's Flame",
                    input = ",1",
                    parent = "Yoshimitsu-3",
                    target = ",h",
                    damage = ",20",
                    startup = ",i22~24",
                    recv = "r23 DGF",
                    tot = "63",
                    crush = ",js26~",
                    block = "+7",
                    hit = "+18g",
                    ch = null,
                    notes = "&lt;div class=&quot;movedata-icon border-purple heat&quot;&gt;Heat Engager&lt;/div&gt;",
                    alias = null,
                    image = null,
                    video = null,
                    alt = null
                )
            )
        )
    )

    private fun createMoveListWithHomingMove() = MoveListResponseDto(
        cargoQuery = listOf(
            MoveListResponseDto.Title(
                title = MoveDto(
                    id = "Yoshimitsu-1+3",
                    name = "Oni Killer",
                    input = "1+3",
                    parent = null,
                    target = "t",
                    damage = "35",
                    startup = "i12~14",
                    recv = "r25",
                    tot = "39",
                    crush = null,
                    block = "-3",
                    hit = "+1d",
                    ch = null,
                    notes = "&lt;div class=&quot;movedata-icon border-blue homing&quot;&gt;Homing&lt;/div&gt;",
                    alias = null,
                    image = null,
                    video = null,
                    alt = null
                )
            )
        )
    )
    //endregion

    private class FakeWavuWikiDataSource : WavuWikiDataSource {
        var moveListResult: Result<MoveListResponseDto, DataError.Remote> =
            Result.Success(MoveListResponseDto(emptyList()))
        var lastCharNameQueried: String? = null

        override suspend fun downloadCharacterList(): Result<CharacterListResponseDto, DataError.Remote> {
            throw NotImplementedError("Not needed for these tests")
        }

        override suspend fun downloadMoveListFor(
            charName: String
        ): Result<MoveListResponseDto, DataError.Remote> {
            lastCharNameQueried = charName
            return moveListResult
        }
    }
}