package io.github.sophon.wikiSuperCombo.usecase

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.WikiError.*
import io.github.sophon.wikiSuperCombo.data.CharacterListResponseDto
import io.github.sophon.wikiSuperCombo.data.MoveDto
import io.github.sophon.wikiSuperCombo.data.MoveListResponseDto
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSource
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DownloadMoveListUseCaseTest {
    //region Success Cases
    @Test
    fun `invoke returns move list when download succeeds`() {
        // given
        val charName = "ryu"
        val dto = MoveListResponseDto(
            cargoQuery = listOf(
                MoveListResponseDto.Title(
                    title = MoveDto(
                        moveId = "1",
                        moveType = "Normal",
                        chara = "Ryu",
                        input = "5LP",
                        name = "Jab",
                        damage = "300",
                        startup = "4",
                        active = "2",
                        recovery = "6",
                        total = "12",
                        guard = "Mid",
                        hitAdv = "+5",
                        blockAdv = "+1",
                        notes = "Fast startup; Good for pressure"
                    )
                ),
                MoveListResponseDto.Title(
                    title = MoveDto(
                        moveId = "2",
                        moveType = "Special",
                        chara = "Ryu",
                        input = "236P",
                        name = "Hadoken",
                        damage = "600",
                        startup = "13",
                        active = "Until contact",
                        recovery = "40",
                        guard = "Mid",
                        hitAdv = "+2",
                        blockAdv = "-2",
                        projSpeed = "1000"
                    )
                )
            )
        )
        val fakeSource = FakeSuperComboDataSource(
            moveListResult = Result.Success(dto)
        )
        val useCase = DownloadMoveListUseCase(fakeSource)

        // when
        val result = runBlocking { useCase.invoke(charName) }

        // then
        assertTrue(result is Result.Success)
        val moves = result.data
        assertEquals(2, moves.size)
        assertEquals("1", moves[0].id)
        assertEquals("5lp", moves[0].input)
        assertEquals("Jab", moves[0].name)
        assertEquals(charName, fakeSource.lastQueriedCharName)
    }

    @Test
    fun `invoke returns empty list when download succeeds with no moves`() {
        // given
        val charName = "ken"
        val dto = MoveListResponseDto(cargoQuery = emptyList())
        val fakeSource = FakeSuperComboDataSource(
            moveListResult = Result.Success(dto)
        )
        val useCase = DownloadMoveListUseCase(fakeSource)

        // when
        val result = runBlocking { useCase.invoke(charName) }

        // then
        assertTrue(result is Result.Success)
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun `invoke filters out template values and dashes`() {
        // given
        val charName = "chun-li"
        val dto = MoveListResponseDto(
            cargoQuery = listOf(
                MoveListResponseDto.Title(
                    title = MoveDto(
                        moveId = "1",
                        moveType = "Normal",
                        chara = "Chun-Li",
                        input = "5MP",
                        name = "Middle Punch",
                        damage = "{{{damage}}}",
                        startup = "-",
                        active = "3",
                        recovery = "10",
                        guard = "Mid",
                        hitAdv = "+4",
                        blockAdv = "0"
                    )
                )
            )
        )
        val fakeSource = FakeSuperComboDataSource(
            moveListResult = Result.Success(dto)
        )
        val useCase = DownloadMoveListUseCase(fakeSource)

        // when
        val result = runBlocking { useCase.invoke(charName) }

        // then
        assertTrue(result is Result.Success)
        val move = result.data[0]
        assertEquals(null, move.damage)
        assertEquals(null, move.startup)
    }

    @Test
    fun `invoke splits notes by semicolon`() {
        // given
        val charName = "guile"
        val dto = MoveListResponseDto(
            cargoQuery = listOf(
                MoveListResponseDto.Title(
                    title = MoveDto(
                        moveId = "1",
                        moveType = "Special",
                        chara = "Guile",
                        input = "46P",
                        name = "Sonic Boom",
                        damage = "600",
                        startup = "8",
                        guard = "Mid",
                        notes = "Projectile; Can be charged; Plus on block"
                    )
                )
            )
        )
        val fakeSource = FakeSuperComboDataSource(
            moveListResult = Result.Success(dto)
        )
        val useCase = DownloadMoveListUseCase(fakeSource)

        // when
        val result = runBlocking { useCase.invoke(charName) }

        // then
        assertTrue(result is Result.Success)
        val move = result.data[0]
        assertEquals(3, move.notes.size)
        assertEquals("Projectile", move.notes[0])
        assertEquals("Can be charged", move.notes[1])
        assertEquals("Plus on block", move.notes[2])
    }
    //endregion

    //region Error Cases
    @Test
    fun `invoke returns download error when download fails with no internet`() {
        // given
        val charName = "cammy"
        val fakeSource = FakeSuperComboDataSource(
            moveListResult = Result.Error(DataError.Remote.NO_INTERNET)
        )
        val useCase = DownloadMoveListUseCase(fakeSource)

        // when
        val result = runBlocking { useCase.invoke(charName) }

        // then
        assertTrue(result is Result.Error)
        assertEquals(DOWNLOAD_ERROR, result.error)
    }

    @Test
    fun `invoke returns download error when download fails with server error`() {
        // given
        val charName = "juri"
        val fakeSource = FakeSuperComboDataSource(
            moveListResult = Result.Error(DataError.Remote.SERVER_ERROR)
        )
        val useCase = DownloadMoveListUseCase(fakeSource)

        // when
        val result = runBlocking { useCase.invoke(charName) }

        // then
        assertTrue(result is Result.Error)
        assertEquals(DOWNLOAD_ERROR, result.error)
    }

    @Test
    fun `invoke returns download error when download fails with request timeout`() {
        // given
        val charName = "dhalsim"
        val fakeSource = FakeSuperComboDataSource(
            moveListResult = Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        )
        val useCase = DownloadMoveListUseCase(fakeSource)

        // when
        val result = runBlocking { useCase.invoke(charName) }

        // then
        assertTrue(result is Result.Error)
        assertEquals(DOWNLOAD_ERROR, result.error)
    }

    @Test
    fun `invoke returns download error when download fails with serialization error`() {
        // given
        val charName = "zangief"
        val fakeSource = FakeSuperComboDataSource(
            moveListResult = Result.Error(DataError.Remote.SERIALIZATION_ERROR)
        )
        val useCase = DownloadMoveListUseCase(fakeSource)

        // when
        val result = runBlocking { useCase.invoke(charName) }

        // then
        assertTrue(result is Result.Error)
        assertEquals(DOWNLOAD_ERROR, result.error)
    }
    //endregion

    //region Test Doubles
    private class FakeSuperComboDataSource(
        private val moveListResult: Result<MoveListResponseDto, DataError.Remote>
    ) : SuperComboDataSource {
        var lastQueriedCharName: String? = null

        override suspend fun downloadCharacterList(): Result<CharacterListResponseDto, DataError.Remote> {
            throw NotImplementedError()
        }

        override suspend fun downloadMoveListFor(charName: String): Result<MoveListResponseDto, DataError.Remote> {
            lastQueriedCharName = charName
            return moveListResult
        }

        override suspend fun getImageUrl(fileName: String): Result<String, DataError.Remote> {
            throw NotImplementedError()
        }
    }
    //endregion
}