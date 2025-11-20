package io.github.sophon.wikiSuperCombo.usecase

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.QueryTable
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.wikiSuperCombo.data.CargoQueryItem
import io.github.sophon.wikiSuperCombo.data.CharacterDto
import io.github.sophon.wikiSuperCombo.data.CharacterListResponseDto
import io.github.sophon.wikiSuperCombo.data.MoveListResponseDto
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSource
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DownloadCharacterListUseCaseTest {
    //region Success Cases
    @Test
    fun `invoke returns character list when download succeeds and all image URLs resolve`() {
        // given
        val dto = CharacterListResponseDto(
            cargoquery = listOf(
                CargoQueryItem(
                    title = CharacterDto(
                        character = "Street Fighter 6/Ryu/Data",
                        chara = "Ryu",
                        name = "Ryu",
                        portrait = "SF6 Ryu Portrait.png",
                        icon = "SF6 Ryu Face.png",
                        hp = "10000",
                        throwRange = "0.8",
                        throwHurtbox = "0.33",
                        fwdWalkSpd = "0.047",
                        bwdWalkSpd = "0.032",
                        fwdDashSpd = "19",
                        bwdDashSpd = "23",
                        fwdDashDist = "1.252",
                        bwdDashDist = "0.923",
                        jumpSpd = "4+38+3",
                        jumpApex = "2.115",
                        fwdJumpDist = "1.90",
                        bwdJumpDist = "1.52",
                        dRushMin = "0.525",
                        dRushBlock = "1.878",
                        dRushMax = "3.628"
                    )
                )
            )
        )
        val imageUrls = mapOf(
            "SF6 Ryu Portrait.png" to "https://example.com/ryu_portrait.png",
            "SF6 Ryu Face.png" to "https://example.com/ryu_icon.png"
        )
        val fakeSource = FakeSuperComboDataSource(
            characterListResult = Result.Success(dto),
            imageUrlResults = imageUrls
        )
        val useCase = DownloadCharacterListUseCase(fakeSource)

        // when
        val result = runBlocking { useCase.invoke(QueryTable("", ""), ) }

        // then
        assertTrue(result is Result.Success)
        val characters = result.data
        assertEquals(1, characters.size)
        assertEquals("ryu", characters[0].id)
        assertEquals("Ryu", characters[0].displayName)
        assertEquals("https://example.com/ryu_icon.png", characters[0].images?.iconUrl)
        assertEquals("https://example.com/ryu_portrait.png", characters[0].images?.bannerUrl)
    }

    @Test
    fun `invoke returns character list when download succeeds but some image URLs fail to resolve`() {
        // given
        val dto = CharacterListResponseDto(
            cargoquery = listOf(
                CargoQueryItem(
                    title = CharacterDto(
                        character = "Street_Fighter_6/Ken",
                        chara = "Ken",
                        name = "Ken",
                        portrait = "ken_portrait.png",
                        icon = "ken_icon.png",
                        hp = "10000",
                        throwRange = "1.25",
                        throwHurtbox = "1.6",
                        fwdWalkSpd = "0.045",
                        bwdWalkSpd = "0.038",
                        fwdDashSpd = "0.093",
                        bwdDashSpd = "0.082",
                        fwdDashDist = "1.37",
                        bwdDashDist = "1.19",
                        jumpSpd = "0.058",
                        jumpApex = "31",
                        fwdJumpDist = "1.97",
                        bwdJumpDist = "1.64",
                        dRushMin = "10",
                        dRushBlock = "12",
                        dRushMax = "15"
                    )
                )
            )
        )
        val imageUrls = mapOf(
            "ken_icon.png" to "https://example.com/ken_icon.png"
        )
        val fakeSource = FakeSuperComboDataSource(
            characterListResult = Result.Success(dto),
            imageUrlResults = imageUrls
        )
        val useCase = DownloadCharacterListUseCase(fakeSource)

        // when
        val result = runBlocking { useCase.invoke(QueryTable("", ""), ) }

        // then
        assertTrue(result is Result.Success)
        val characters = result.data
        assertEquals(1, characters.size)
        assertEquals("https://example.com/ken_icon.png", characters[0].images?.iconUrl)
        assertEquals(null, characters[0].images?.bannerUrl)
    }

    @Test
    fun `invoke returns empty list when download succeeds with empty character list`() {
        // given
        val dto = CharacterListResponseDto(cargoquery = emptyList())
        val fakeSource = FakeSuperComboDataSource(
            characterListResult = Result.Success(dto),
            imageUrlResults = emptyMap()
        )
        val useCase = DownloadCharacterListUseCase(fakeSource)

        // when
        val result = runBlocking { useCase.invoke(QueryTable("", ""), ) }

        // then
        assertTrue(result is Result.Success)
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun `invoke deduplicates image URLs when multiple characters share same images`() {
        // given
        val dto = CharacterListResponseDto(
            cargoquery = listOf(
                CargoQueryItem(
                    title = CharacterDto(
                        character = "Street_Fighter_6/Ryu",
                        chara = "Ryu",
                        name = "Ryu",
                        portrait = "shared_portrait.png",
                        icon = "shared_icon.png",
                        hp = "10000",
                        throwRange = "1.25",
                        throwHurtbox = "1.6",
                        fwdWalkSpd = "0.045",
                        bwdWalkSpd = "0.038",
                        fwdDashSpd = "0.093",
                        bwdDashSpd = "0.082",
                        fwdDashDist = "1.37",
                        bwdDashDist = "1.19",
                        jumpSpd = "0.058",
                        jumpApex = "31",
                        fwdJumpDist = "1.97",
                        bwdJumpDist = "1.64",
                        dRushMin = "10",
                        dRushBlock = "12",
                        dRushMax = "15"
                    )
                ),
                CargoQueryItem(
                    title = CharacterDto(
                        character = "Street_Fighter_6/Ken",
                        chara = "Ken",
                        name = "Ken",
                        portrait = "shared_portrait.png",
                        icon = "shared_icon.png",
                        hp = "10000",
                        throwRange = "1.25",
                        throwHurtbox = "1.6",
                        fwdWalkSpd = "0.045",
                        bwdWalkSpd = "0.038",
                        fwdDashSpd = "0.093",
                        bwdDashSpd = "0.082",
                        fwdDashDist = "1.37",
                        bwdDashDist = "1.19",
                        jumpSpd = "0.058",
                        jumpApex = "31",
                        fwdJumpDist = "1.97",
                        bwdJumpDist = "1.64",
                        dRushMin = "10",
                        dRushBlock = "12",
                        dRushMax = "15"
                    )
                )
            )
        )
        val fakeSource = FakeSuperComboDataSource(
            characterListResult = Result.Success(dto),
            imageUrlResults = mapOf(
                "shared_portrait.png" to "https://example.com/shared_portrait.png",
                "shared_icon.png" to "https://example.com/shared_icon.png"
            )
        )
        val useCase = DownloadCharacterListUseCase(fakeSource)

        // when
        val result = runBlocking { useCase.invoke(QueryTable("", ""), ) }

        // then
        assertTrue(result is Result.Success)
        assertEquals(1, fakeSource.imageUrlRequestCount)
        assertEquals(2, fakeSource.lastRequestedFileNames?.size)
    }
    //endregion

    //region Error Cases
    @Test
    fun `invoke returns download error when character list download fails with no internet`() {
        // given
        val fakeSource = FakeSuperComboDataSource(
            characterListResult = Result.Error(DataError.Remote.NO_INTERNET)
        )
        val useCase = DownloadCharacterListUseCase(fakeSource)

        // when
        val result = runBlocking { useCase.invoke(QueryTable("", ""), ) }

        // then
        assertTrue(result is Result.Error)
        assertTrue(result.error is WikiError.DownloadError)
    }

    @Test
    fun `invoke returns download error when character list download fails with server error`() {
        // given
        val fakeSource = FakeSuperComboDataSource(
            characterListResult = Result.Error(DataError.Remote.SERVER_ERROR)
        )
        val useCase = DownloadCharacterListUseCase(fakeSource)

        // when
        val result = runBlocking { useCase.invoke(QueryTable("", ""), ) }

        // then
        assertTrue(result is Result.Error)
        assertTrue(result.error is WikiError.DownloadError)
    }

    @Test
    fun `invoke returns download error when character list download fails with request timeout`() {
        // given
        val fakeSource = FakeSuperComboDataSource(
            characterListResult = Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        )
        val useCase = DownloadCharacterListUseCase(fakeSource)

        // when
        val result = runBlocking { useCase.invoke(QueryTable("", "")) }

        // then
        assertTrue(result is Result.Error)
        assertTrue(result.error is WikiError.DownloadError)
    }

    @Test
    fun `invoke returns download error when character list download fails with serialization error`() {
        // given
        val fakeSource = FakeSuperComboDataSource(
            characterListResult = Result.Error(DataError.Remote.SERIALIZATION_ERROR)
        )
        val useCase = DownloadCharacterListUseCase(fakeSource)

        // when
        val result = runBlocking { useCase.invoke(QueryTable("", ""), ) }

        // then
        assertTrue(result is Result.Error)
        assertTrue(result.error is WikiError.DownloadError)
    }
    //endregion

    //region Test Doubles
    private class FakeSuperComboDataSource(
        private val characterListResult: Result<CharacterListResponseDto, DataError.Remote>,
        private val imageUrlResults: Map<String, String> = emptyMap()
    ) : SuperComboDataSource {
        var imageUrlRequestCount = 0
        var lastRequestedFileNames: List<String>? = null

        override suspend fun downloadCharacterList(table: String): Result<CharacterListResponseDto, DataError.Remote> {
            return characterListResult
        }

        override suspend fun downloadMoveList(
            table: String,
            charName: String
        ): Result<MoveListResponseDto, DataError.Remote> {
            throw NotImplementedError()
        }

        override suspend fun getImageUrl(
            fileNames: List<String>
        ): Result<Map<String, String>, DataError.Remote> {
            imageUrlRequestCount++
            lastRequestedFileNames = fileNames

            if (fileNames.isEmpty()) return Result.Success(emptyMap())

            val results = fileNames.mapNotNull { fileName ->
                imageUrlResults[fileName]?.let { fileName to it }
            }.toMap()

            return Result.Success(results)  // Return empty map if no matches found
        }
    }
    //endregion
}