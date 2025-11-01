package com.example.wikiwavu.usecase

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import com.example.wikiwavu.data.MoveDto
import com.example.wikiwavu.data.MoveListResponseDto
import com.example.wikiwavu.data.WavuWikiDataSource
import com.example.wikiwavu.domain.model.Character
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class DownloadMoveListUseCaseTekken8Test {
    private lateinit var mockDataSource: MockWavuWikiDataSource
    private lateinit var useCase: DownloadMoveListUseCase

    @BeforeTest
    fun setup() {
        mockDataSource = MockWavuWikiDataSource()
        useCase = DownloadMoveListUseCase(mockDataSource)
    }

    // region Properties - Never Null for Tekken 8
    @Test
    fun `invoke ensures all boolean properties are never null for Tekken 8 moves`() = runTest {
        // Given
        val character = createTestCharacter("Jin")
        val moveDto = MoveDto(
            id = "Jin-1",
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
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val move = result.data.first()

        assertThat(move.properties.isHeat).isNotNull()
        assertThat(move.properties.isPowerCrush).isNotNull()
        assertThat(move.properties.isHoming).isNotNull()
        assertThat(move.properties.isHeat).isEqualTo(false)
        assertThat(move.properties.isPowerCrush).isEqualTo(false)
        assertThat(move.properties.isHoming).isEqualTo(false)
    }

    @Test
    fun `invoke sets heat to true when move is heat engager`() = runTest {
        // Given
        val character = createTestCharacter("Jin")
        val moveDto = MoveDto(
            id = "Jin-df2",
            input = "d/f+2",
            parent = null,
            target = "m",
            damage = "15",
            startup = "i15",
            recv = null,
            tot = null,
            crush = null,
            block = "-12",
            hit = "Launch",
            ch = null,
            notes = "Heat Engager on block or hit",
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val move = result.data.first()

        assertThat(move.properties.isHeat).isNotNull()
        assertThat(move.properties.isHeat).isEqualTo(true)
        assertThat(move.properties.isPowerCrush).isEqualTo(false)
        assertThat(move.properties.isHoming).isEqualTo(false)
    }

    @Test
    fun `invoke sets power crush to true when crush contains pc`() = runTest {
        // Given
        val character = createTestCharacter("Paul")
        val moveDto = MoveDto(
            id = "Paul-f23",
            input = "f+2,3",
            parent = null,
            target = "m,m",
            damage = "12,20",
            startup = "i18",
            recv = null,
            tot = null,
            crush = "pc8~",
            block = "-12",
            hit = "+5a",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val move = result.data.first()

        assertThat(move.properties.isPowerCrush).isNotNull()
        assertThat(move.properties.isPowerCrush).isEqualTo(true)
        assertThat(move.properties.isHeat).isEqualTo(false)
        assertThat(move.properties.isHoming).isEqualTo(false)
    }

    @Test
    fun `invoke sets homing to true when notes contain homing`() = runTest {
        // Given
        val character = createTestCharacter("Jin")
        val moveDto = MoveDto(
            id = "Jin-f4",
            input = "f+4",
            parent = null,
            target = "m",
            damage = "20",
            startup = "i15",
            recv = null,
            tot = null,
            crush = null,
            block = "-9",
            hit = "+2",
            ch = null,
            notes = "Homing move that tracks sidewalk",
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val move = result.data.first()

        assertThat(move.properties.isHoming).isNotNull()
        assertThat(move.properties.isHoming).isEqualTo(true)
        assertThat(move.properties.isHeat).isEqualTo(false)
        assertThat(move.properties.isPowerCrush).isEqualTo(false)
    }

    @Test
    fun `invoke handles move with multiple properties set to true`() = runTest {
        // Given
        val character = createTestCharacter("Jin")
        val moveDto = MoveDto(
            id = "Jin-f1+2",
            input = "f+1+2",
            parent = null,
            target = "m",
            damage = "25",
            startup = "i20",
            recv = null,
            tot = null,
            crush = "pc12~",
            block = "-15",
            hit = "KND",
            ch = null,
            notes = "Heat Engager and Homing attack",
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val move = result.data.first()

        assertThat(move.properties.isHeat).isEqualTo(true)
        assertThat(move.properties.isPowerCrush).isEqualTo(true)
        assertThat(move.properties.isHoming).isEqualTo(true)
    }
    // endregion

    // region Stance Detection - Valid Stances
    @Test
    fun `invoke correctly detects stance from input with three letters and digit`() = runTest {
        // Given
        val character = createTestCharacter("Jin")
        val moveDto = MoveDto(
            id = "Jin-zen.1",
            input = "zen.1",
            parent = null,
            target = "h",
            damage = "12",
            startup = "i12",
            recv = null,
            tot = null,
            crush = null,
            block = "+2",
            hit = "+8",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val move = result.data.first()

        assertThat(move.properties.stance).isEqualTo("zen")
    }

    @Test
    fun `invoke correctly detects stance from des dot f21 input`() = runTest {
        // Given
        val character = createTestCharacter("Yoshimitsu")
        val moveDto = MoveDto(
            id = "Yoshimitsu-des.f21",
            input = "des.f+2,1",
            parent = null,
            target = "m,h",
            damage = "15,10",
            startup = "i18",
            recv = null,
            tot = null,
            crush = null,
            block = "-5",
            hit = "+6",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val move = result.data.first()

        assertThat(move.properties.stance).isEqualTo("des")
    }

    @Test
    fun `invoke correctly detects stance from kin dot 1+2 input`() = runTest {
        // Given
        val character = createTestCharacter("Yoshimitsu")
        val moveDto = MoveDto(
            id = "Yoshimitsu-kin.1+2",
            input = "kin.1+2",
            parent = null,
            target = "m",
            damage = "20",
            startup = "i15",
            recv = null,
            tot = null,
            crush = null,
            block = "-10",
            hit = "KND",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val move = result.data.first()

        assertThat(move.properties.stance).isEqualTo("kin")
    }

    @Test
    fun `invoke correctly detects stance from fle dot 3+4 input`() = runTest {
        // Given
        val character = createTestCharacter("Yoshimitsu")
        val moveDto = MoveDto(
            id = "Yoshimitsu-fle.3+4",
            input = "fle.3+4",
            parent = null,
            target = "L",
            damage = "22",
            startup = "i20",
            recv = null,
            tot = null,
            crush = null,
            block = "-15",
            hit = "KND",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val move = result.data.first()

        assertThat(move.properties.stance).isEqualTo("fle")
    }

    @Test
    fun `invoke correctly detects uppercase stance`() = runTest {
        // Given
        val character = createTestCharacter("Jin")
        val moveDto = MoveDto(
            id = "Jin-ZEN.2",
            input = "ZEN.2",
            parent = null,
            target = "m",
            damage = "18",
            startup = "i14",
            recv = null,
            tot = null,
            crush = null,
            block = "-8",
            hit = "+3",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val move = result.data.first()

        assertThat(move.properties.stance).isEqualTo("zen")
    }
    // endregion

    // region Stance Detection - Not Stances
    @Test
    fun `invoke returns empty stance for motion input wr`() = runTest {
        // Given
        val character = createTestCharacter("Jin")
        val moveDto = MoveDto(
            id = "Jin-wr2",
            input = "wr2",
            parent = null,
            target = "m",
            damage = "22",
            startup = "i15",
            recv = null,
            tot = null,
            crush = null,
            block = "-10",
            hit = "KND",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val move = result.data.first()

        assertThat(move.properties.stance).isEqualTo("")
    }

    @Test
    fun `invoke returns empty stance for motion input ff`() = runTest {
        // Given
        val character = createTestCharacter("Jin")
        val moveDto = MoveDto(
            id = "Jin-ff3",
            input = "ff3",
            parent = null,
            target = "m",
            damage = "25",
            startup = "i20",
            recv = null,
            tot = null,
            crush = null,
            block = "-12",
            hit = "Launch",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val move = result.data.first()

        assertThat(move.properties.stance).isEqualTo("")
    }

    @Test
    fun `invoke returns empty stance for motion input qcf`() = runTest {
        // Given
        val character = createTestCharacter("Akuma")
        val moveDto = MoveDto(
            id = "Akuma-qcf1",
            input = "qcf1",
            parent = null,
            target = "h",
            damage = "20",
            startup = "i14",
            recv = null,
            tot = null,
            crush = null,
            block = "-8",
            hit = "+3",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val move = result.data.first()

        assertThat(move.properties.stance).isEqualTo("")
    }

    @Test
    fun `invoke returns empty stance for motion input qcb`() = runTest {
        // Given
        val character = createTestCharacter("Akuma")
        val moveDto = MoveDto(
            id = "Akuma-qcb2",
            input = "qcb2",
            parent = null,
            target = "m",
            damage = "18",
            startup = "i16",
            recv = null,
            tot = null,
            crush = null,
            block = "-9",
            hit = "+2",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val move = result.data.first()

        assertThat(move.properties.stance).isEqualTo("")
    }

    @Test
    fun `invoke returns empty stance for input without digits`() = runTest {
        // Given
        val character = createTestCharacter("Jin")
        val moveDto = MoveDto(
            id = "Jin-zen",
            input = "zen",
            parent = null,
            target = null,
            damage = null,
            startup = null,
            recv = null,
            tot = null,
            crush = null,
            block = null,
            hit = null,
            ch = null,
            notes = "Transition to zen stance",
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val move = result.data.first()

        assertThat(move.properties.stance).isEqualTo("")
    }

    @Test
    fun `invoke returns empty stance for input with less than three letters`() = runTest {
        // Given
        val character = createTestCharacter("Jin")
        val moveDto = MoveDto(
            id = "Jin-df2",
            input = "d/f+2",
            parent = null,
            target = "m",
            damage = "15",
            startup = "i15",
            recv = null,
            tot = null,
            crush = null,
            block = "-12",
            hit = "Launch",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val move = result.data.first()

        assertThat(move.properties.stance).isEqualTo("")
    }

    @Test
    fun `invoke returns empty stance for regular move starting with digit`() = runTest {
        // Given
        val character = createTestCharacter("Jin")
        val moveDto = MoveDto(
            id = "Jin-1",
            input = "1",
            parent = null,
            target = "h",
            damage = "5",
            startup = "i10",
            recv = null,
            tot = null,
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
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val move = result.data.first()

        assertThat(move.properties.stance).isEqualTo("")
    }

    @Test
    fun `invoke returns empty stance for input with special characters only`() = runTest {
        // Given
        val character = createTestCharacter("Jin")
        val moveDto = MoveDto(
            id = "Jin-1+2",
            input = "1+2",
            parent = null,
            target = "m",
            damage = "20",
            startup = "i14",
            recv = null,
            tot = null,
            crush = null,
            block = "-8",
            hit = "+3",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val move = result.data.first()

        assertThat(move.properties.stance).isEqualTo("")
    }
    // endregion

    // region Stance Detection - Edge Cases
    @Test
    fun `invoke handles stance with complex notation after stance name`() = runTest {
        // Given
        val character = createTestCharacter("Yoshimitsu")
        val moveDto = MoveDto(
            id = "Yoshimitsu-des.bt.1",
            input = "des.bt.1",
            parent = null,
            target = "h",
            damage = "10",
            startup = "i12",
            recv = null,
            tot = null,
            crush = null,
            block = "+2",
            hit = "+8",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val move = result.data.first()

        assertThat(move.properties.stance).isEqualTo("des")
    }

    @Test
    fun `invoke detects stance with numbers in the middle`() = runTest {
        // Given
        val character = createTestCharacter("TestChar")
        val moveDto = MoveDto(
            id = "TestChar-abc.3+4",
            input = "abc.3+4",
            parent = null,
            target = "m",
            damage = "20",
            startup = "i15",
            recv = null,
            tot = null,
            crush = null,
            block = "-10",
            hit = "+5",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val move = result.data.first()

        assertThat(move.properties.stance).isEqualTo("abc")
    }

    @Test
    fun `invoke handles multiple moves with different stances`() = runTest {
        // Given
        val character = createTestCharacter("Yoshimitsu")
        val move1 = MoveDto(
            id = "Yoshimitsu-kin.1",
            input = "kin.1",
            parent = null,
            target = "h",
            damage = "10",
            startup = "i10",
            recv = null,
            tot = null,
            crush = null,
            block = "+2",
            hit = "+8",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val move2 = MoveDto(
            id = "Yoshimitsu-des.2",
            input = "des.2",
            parent = null,
            target = "m",
            damage = "15",
            startup = "i15",
            recv = null,
            tot = null,
            crush = null,
            block = "-5",
            hit = "+3",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val move3 = MoveDto(
            id = "Yoshimitsu-fle.4",
            input = "fle.4",
            parent = null,
            target = "L",
            damage = "20",
            startup = "i20",
            recv = null,
            tot = null,
            crush = null,
            block = "-12",
            hit = "KND",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(
            createMoveListResponse(listOf(move1, move2, move3))
        )

        // When
        val result = useCase.invoke(character.name)

        // Then
        result as Result.Success
        val moves = result.data

        assertThat(moves).hasSize(3)
        assertThat(moves[0].properties.stance).isEqualTo("kin")
        assertThat(moves[1].properties.stance).isEqualTo("des")
        assertThat(moves[2].properties.stance).isEqualTo("fle")
    }
    // endregion

    // region Helper Methods
    private fun createTestCharacter(name: String) = Character(
        name = name,
        portraitUrl = "https://example.com/$name.png",
        wavuPageUrl = "https://wavu.wiki/$name",
        alias = listOf(name.lowercase())
    )

    private fun createMoveListResponse(moves: List<MoveDto>) = MoveListResponseDto(
        cargoQuery = moves.map { MoveListResponseDto.Title(it) }
    )
    // endregion

    // region Mock Data Source
    private class MockWavuWikiDataSource : WavuWikiDataSource {
        var mockResponse: Result<MoveListResponseDto, DataError.Remote>? = null

        override suspend fun fetchMoveList(char: String): Result<MoveListResponseDto, DataError.Remote> {
            return mockResponse ?: Result.Error(DataError.Remote.UNKNOWN)
        }
    }
    // endregion
}