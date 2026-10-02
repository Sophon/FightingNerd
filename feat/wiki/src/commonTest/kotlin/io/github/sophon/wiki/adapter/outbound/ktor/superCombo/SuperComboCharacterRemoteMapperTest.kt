package io.github.sophon.wiki.adapter.outbound.ktor.superCombo

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.gameProperties.SFCharProperties
import kotlin.test.Test

class SuperComboCharacterRemoteMapperTest {

    @Test
    fun `character data maps to a character`() {
        //given
        val dto = SuperComboCharacterSource.ken
        val expected = Character(
            id = CharacterId(Game.StreetFighter6, "Ken"),
            displayName = "Ken",
            remoteQueryId = "Ken",
            wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Ken",
            aliasList = emptyList(),
            images = Character.Images(iconId = "SF6_Ken_Face.png"),
            hp = "10000",
            gameProperties = SFCharProperties(
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
        )

        //when
        val result = dto.toCharacter(Game.StreetFighter6)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `dotted name keeps the dot in the id`() {
        //given
        val dto = SuperComboCharacterSource.cViper
        val expected = Character(
            id = CharacterId(Game.StreetFighter6, "C.Viper"),
            displayName = "C. Viper",
            remoteQueryId = "C.Viper",
            wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/C.Viper",
            aliasList = listOf("cv", "viper"),
            images = Character.Images(iconId = "SF6 Cviper Face.png"),
            hp = "10000",
            gameProperties = SFCharProperties(
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
        )

        //when
        val result = dto.toCharacter(Game.StreetFighter6)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `hyphenated name keeps the hyphen in the id`() {
        //given
        val dto = SuperComboCharacterSource.chunLi
        val expected = Character(
            id = CharacterId(Game.StreetFighter6, "Chun-Li"),
            displayName = "Chun-Li",
            remoteQueryId = "Chun-Li",
            wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Chun-Li",
            aliasList = listOf("cl", "chun", "li"),
            images = Character.Images(iconId = "SF6 Chun-Li Face.png"),
            hp = "10000",
            gameProperties = SFCharProperties(
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
            ),
        )

        //when
        val result = dto.toCharacter(Game.StreetFighter6)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `two-word name keeps the wiki's underscore in the id`() {
        //given
        val dto = SuperComboCharacterSource.deeJay
        val expected = Character(
            id = CharacterId(Game.StreetFighter6, "Dee_Jay"),
            displayName = "Dee Jay",
            remoteQueryId = "Dee_Jay",
            wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Dee_Jay",
            aliasList = listOf("dj", "dee", "jay"),
            images = Character.Images(iconId = "SF6 Dee_Jay Face.png"),
            hp = "10000",
            gameProperties = SFCharProperties(
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
        )

        //when
        val result = dto.toCharacter(Game.StreetFighter6)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `icon and portrait urls come from the image map`() {
        //given
        val dto = SuperComboCharacterSource.ken
        val imageUrlMap = mapOf(
            "SF6 Ken Portrait.png" to "https://wiki.supercombo.gg/images/SF6_Ken_Portrait.png",
            "SF6_Ken_Face.png" to "https://wiki.supercombo.gg/images/SF6_Ken_Face.png",
        )
        val expected = Character.Images(
            iconId = "SF6_Ken_Face.png",
            iconUrl = "https://wiki.supercombo.gg/images/SF6_Ken_Face.png",
            bannerUrl = "https://wiki.supercombo.gg/images/SF6_Ken_Portrait.png",
        )

        //when
        val result = dto.toCharacter(Game.StreetFighter6, imageUrlMap)

        //then
        assertThat(result.images).isEqualTo(expected)
    }
}

private fun CharacterDto.toCharacter(
    game: Game,
    imageUrlMap: Map<String, String> = emptyMap(),
): Character {
    val responseDto = SuperComboCharacterListResponseDto(cargoquery = listOf(CargoQueryItem(this)))
    val character = responseDto.toDomain(game, imageUrlMap).single()
    return character
}

private object SuperComboCharacterSource {
    val ken = CharacterDto(
        Character = "Street Fighter 6/Ken/Data",
        chara = "Ken",
        name = "Ken",
        portrait = "SF6 Ken Portrait.png",
        icon = "SF6_Ken_Face.png",
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
        dRushMax = "3.590",
    )
    val cViper = CharacterDto(
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
        dRushMax = "3.355",
    )
    val chunLi = CharacterDto(
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
        dRushMax = "3.163",
    )
    val deeJay = CharacterDto(
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
        dRushMax = "2.713",
    )
}
