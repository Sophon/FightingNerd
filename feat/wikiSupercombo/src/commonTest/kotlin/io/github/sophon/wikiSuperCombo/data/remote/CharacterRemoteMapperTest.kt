package io.github.sophon.wikiSuperCombo.data.remote

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import io.github.sophon.core.wiki.model.Character
import kotlin.test.Test

class CharacterRemoteMapperTest {
    val gameId = "Street_Fighter_6"

    @Test
    fun `toDomain should return properly formatted character`() {
        // given
        val characterDto = CharacterDto(
            Character = "Street Fighter 6/Ken/Data",
            chara = "Ken",
            name = "Ken",
            portrait = "SF6 Ken Portrait.png",
            icon = "SF6 Ken Face.png",
            hp = "10000",
            throwRange = "0.8",
            throwHurtbox = "0.33",
            fwdWalkSpd = "0.047",
            bwdWalkSpd = "0.032",
            fwdDashSpd = "19",
            bwdDashSpd = "23",
            fwdDashDist = "1.322",
            bwdDashDist = "0.923",
            jumpSpd = "4+38+3",
            jumpApex = "2.115",
            fwdJumpDist = "1.90",
            bwdJumpDist = "1.52",
            dRushMin = "0.745",
            dRushBlock = "2.449",
            dRushMax = "3.590"
        )
        val responseDto = CharacterListResponseDto(
            cargoquery = listOf(CargoQueryItem(characterDto))
        )
        val expectedCharacter = Character(
            id = "ken",
            displayName = "Ken",
            remoteQueryId = "Ken",
            wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Ken",
            aliasList = emptyList(),
            images = Character.Images(iconUrl = null, bannerUrl = null),
            sf6Properties = Character.SF6Properties(
                fwdWalkSpd = "0.047",
                bwdWalkSpd = "0.032",
                fwdDashSpd = "19",
                bwdDashSpd = "23",
                fwdDashDist = "1.322",
                bwdDashDist = "0.923",
                dRushMin = "0.745",
                dRushBlock = "2.449",
                dRushMax = "3.590",
                throwRange = "0.8",
                throwHurtbox = "0.33",
                jumpSpd = "4+38+3",
                jumpApex = "2.115",
                fwdJumpDist = "1.90",
                bwdJumpDist = "1.52",
            ),
            hp = "10000"
        )

        // when
        val result = responseDto.toDomain("Street_Fighter_6", emptyMap())

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(expectedCharacter)
    }

    @Test
    fun `toDomain should map multi word character names with aliases`() {
        // given
        val cViperDto = CharacterDto(
            Character = "Street Fighter 6/C.Viper/Data",
            chara = "C.Viper",
            name = "C. Viper",
            portrait = "SF6 Cviper Portrait.png",
            icon = "SF6 Cviper Face.png",
            hp = "10000",
            throwRange = "0.8",
            throwHurtbox = "0.33",
            fwdWalkSpd = "0.0452",
            bwdWalkSpd = "0.031",
            fwdDashSpd = "21",
            bwdDashSpd = "23",
            fwdDashDist = "1.50",
            bwdDashDist = "0.80",
            jumpSpd = "4+38+3<br>(6+40+3)",
            jumpApex = "2.11<br>(2.195)",
            fwdJumpDist = "1.90<br>(3.00)",
            bwdJumpDist = "1.52",
            dRushMin = "0.374",
            dRushBlock = "1.756",
            dRushMax = "3.355"
        )
        val chunLiDto = CharacterDto(
            Character = "Street Fighter 6/Chun-Li/Data",
            chara = "Chun-Li",
            name = "Chun-Li",
            portrait = "SF6 Chun-Li Portrait.png",
            icon = "SF6 Chun-Li Face.png",
            hp = "10000",
            throwRange = "0.8",
            throwHurtbox = "0.33",
            fwdWalkSpd = "0.050",
            bwdWalkSpd = "0.035",
            fwdDashSpd = "19",
            bwdDashSpd = "25",
            fwdDashDist = "1.508",
            bwdDashDist = "1.211",
            jumpSpd = "4+42+3",
            jumpApex = "2.247",
            fwdJumpDist = "2.10",
            bwdJumpDist = "1.68",
            dRushMin = "1.044",
            dRushBlock = "2.222",
            dRushMax = "3.163"
        )
        val deeJayDto = CharacterDto(
            Character = "Street Fighter 6/Dee Jay/Data",
            chara = "Dee_Jay",
            name = "Dee Jay",
            portrait = "SF6 Dee_Jay Portrait.png",
            icon = "SF6 Dee_Jay Face.png",
            hp = "10000",
            throwRange = "0.8",
            throwHurtbox = "0.33",
            fwdWalkSpd = "0.043",
            bwdWalkSpd = "0.032",
            fwdDashSpd = "19",
            bwdDashSpd = "23",
            fwdDashDist = "1.50",
            bwdDashDist = "0.90",
            jumpSpd = "4+38+3",
            jumpApex = "2.115",
            fwdJumpDist = "1.90",
            bwdJumpDist = "1.52",
            dRushMin = "0.763",
            dRushBlock = "2.535",
            dRushMax = "2.713"
        )
        val responseDto = CharacterListResponseDto(
            cargoquery = listOf(
                CargoQueryItem(cViperDto),
                CargoQueryItem(chunLiDto),
                CargoQueryItem(deeJayDto)
            )
        )
        val expectedCViper = Character(
            id = "cviper",
            displayName = "C. Viper",
            remoteQueryId = "C.Viper",
            wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/C.Viper",
            aliasList = listOf("cv", "viper"),
            images = Character.Images(iconUrl = null, bannerUrl = null),
            sf6Properties = Character.SF6Properties(
                fwdWalkSpd = "0.0452",
                bwdWalkSpd = "0.031",
                fwdDashSpd = "21",
                bwdDashSpd = "23",
                fwdDashDist = "1.50",
                bwdDashDist = "0.80",
                dRushMin = "0.374",
                dRushBlock = "1.756",
                dRushMax = "3.355",
                throwRange = "0.8",
                throwHurtbox = "0.33",
                jumpSpd = "4+38+3<br>(6+40+3)",
                jumpApex = "2.11<br>(2.195)",
                fwdJumpDist = "1.90<br>(3.00)",
                bwdJumpDist = "1.52",
            ),
            hp = "10000"
        )
        val expectedChunLi = Character(
            id = "chun_li",
            displayName = "Chun-Li",
            remoteQueryId = "Chun-Li",
            wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Chun-Li",
            aliasList = listOf("cl", "chun", "li"),
            images = Character.Images(iconUrl = null, bannerUrl = null),
            hp = "10000",
            sf6Properties = Character.SF6Properties(
                fwdWalkSpd = "0.050",
                bwdWalkSpd = "0.035",
                fwdDashSpd = "19",
                bwdDashSpd = "25",
                fwdDashDist = "1.508",
                bwdDashDist = "1.211",
                dRushMin = "1.044",
                dRushBlock = "2.222",
                dRushMax = "3.163",
                throwRange = "0.8",
                throwHurtbox = "0.33",
                jumpSpd = "4+42+3",
                jumpApex = "2.247",
                fwdJumpDist = "2.10",
                bwdJumpDist = "1.68",
            )
        )
        val expectedDeeJay = Character(
            id = "dee_jay",
            displayName = "Dee Jay",
            remoteQueryId = "Dee_Jay",
            wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Dee_Jay",
            aliasList = listOf("dj", "dee", "jay"),
            images = Character.Images(iconUrl = null, bannerUrl = null),
            sf6Properties = Character.SF6Properties(
                fwdWalkSpd = "0.043",
                bwdWalkSpd = "0.032",
                fwdDashSpd = "19",
                bwdDashSpd = "23",
                fwdDashDist = "1.50",
                bwdDashDist = "0.90",
                dRushMin = "0.763",
                dRushBlock = "2.535",
                dRushMax = "2.713",
                throwRange = "0.8",
                throwHurtbox = "0.33",
                jumpSpd = "4+38+3",
                jumpApex = "2.115",
                fwdJumpDist = "1.90",
                bwdJumpDist = "1.52",
            ),
            hp = "10000"
        )

        // when
        val result = responseDto.toDomain(gameId, emptyMap())

        // then
        assertThat(result).hasSize(3)
        assertThat(result[0]).isEqualTo(expectedCViper)
        assertThat(result[1]).isEqualTo(expectedChunLi)
        assertThat(result[2]).isEqualTo(expectedDeeJay)
    }

    @Test
    fun `toDomain should map character with image URLs from map`() {
        // given
        val characterDto = CharacterDto(
            Character = "Street Fighter 6/Ken/Data",
            chara = "Ken",
            name = "Ken",
            portrait = "SF6 Ken Portrait.png",
            icon = "SF6 Ken Face.png",
            hp = "10000",
            throwRange = "0.8",
            throwHurtbox = "0.33",
            fwdWalkSpd = "0.047",
            bwdWalkSpd = "0.032",
            fwdDashSpd = "19",
            bwdDashSpd = "23",
            fwdDashDist = "1.322",
            bwdDashDist = "0.923",
            jumpSpd = "4+38+3",
            jumpApex = "2.115",
            fwdJumpDist = "1.90",
            bwdJumpDist = "1.52",
            dRushMin = "0.745",
            dRushBlock = "2.449",
            dRushMax = "3.590"
        )
        val responseDto = CharacterListResponseDto(
            cargoquery = listOf(CargoQueryItem(characterDto))
        )
        val imageUrlMap = mapOf(
            "SF6 Ken Portrait.png" to "https://example.com/portrait.png",
            "SF6 Ken Face.png" to "https://example.com/icon.png"
        )
        val expectedCharacter = Character(
            id = "ken",
            displayName = "Ken",
            remoteQueryId = "Ken",
            wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Ken",
            aliasList = emptyList(),
            images = Character.Images(
                iconUrl = "https://example.com/icon.png",
                bannerUrl = "https://example.com/portrait.png"
            ),
            sf6Properties = Character.SF6Properties(
                fwdWalkSpd = "0.047",
                bwdWalkSpd = "0.032",
                fwdDashSpd = "19",
                bwdDashSpd = "23",
                fwdDashDist = "1.322",
                bwdDashDist = "0.923",
                dRushMin = "0.745",
                dRushBlock = "2.449",
                dRushMax = "3.590",
                throwRange = "0.8",
                throwHurtbox = "0.33",
                jumpSpd = "4+38+3",
                jumpApex = "2.115",
                fwdJumpDist = "1.90",
                bwdJumpDist = "1.52",
            ),
            hp = "10000",
        )

        // when
        val result = responseDto.toDomain(gameId, imageUrlMap)

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(expectedCharacter)
    }
}
