package io.github.sophon.wiki.adapter.outbound.ktor.wavu

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.wiki.Game
import kotlin.test.Test

class WavuCharacterRemoteMapperTest {

    @Test
    fun `every listed character is mapped in order`() {
        //given
        val responseDto = WavuCharacterListResponseDto(characters = listOf(armorKing, jack8))
        val expected = listOf("Armor King", "Jack-8")

        //when
        val result = responseDto.toDomain(Game.Tekken8)

        //then
        assertThat(result.map { it.displayName }).isEqualTo(expected)
    }

    //region id
    @Test
    fun `id is the display name - the service normalizes it`() {
        //given
        val responseDto = WavuCharacterListResponseDto(characters = listOf(armorKing))
        val expected = CharacterId(Game.Tekken8, "Armor King")

        //when
        val result = responseDto.toDomain(Game.Tekken8).single()

        //then
        assertThat(result.id).isEqualTo(expected)
    }
    //endregion

    //region query
    @Test
    fun `query name is the display name`() {
        //given
        val responseDto = WavuCharacterListResponseDto(characters = listOf(armorKing))
        val expected = "Armor King"

        //when
        val result = responseDto.toDomain(Game.Tekken8).single()

        //then
        assertThat(result.remoteQueryId).isEqualTo(expected)
    }
    //endregion

    //region wiki url
    @Test
    fun `wiki url joins the wavu name by underscore`() {
        //given
        val responseDto = WavuCharacterListResponseDto(characters = listOf(armorKing))
        val expected = "https://wavu.wiki/t/Armor_King"

        //when
        val result = responseDto.toDomain(Game.Tekken8).single()

        //then
        assertThat(result.wikiUrl).isEqualTo(expected)
    }

    @Test
    fun `wiki url keeps hyphens`() {
        //given
        val responseDto = WavuCharacterListResponseDto(characters = listOf(jack8))
        val expected = "https://wavu.wiki/t/Jack-8"

        //when
        val result = responseDto.toDomain(Game.Tekken8).single()

        //then
        assertThat(result.wikiUrl).isEqualTo(expected)
    }
    //endregion

    //region aliases
    @Test
    fun `aliases are kept as listed`() {
        //given
        val responseDto = WavuCharacterListResponseDto(characters = listOf(armorKing))
        val expected = listOf("ak", "aking", "armorking")

        //when
        val result = responseDto.toDomain(Game.Tekken8).single()

        //then
        assertThat(result.aliasList).isEqualTo(expected)
    }
    //endregion

    //region images
    @Test
    fun `icon is the official large png and its file name`() {
        //given
        val responseDto = WavuCharacterListResponseDto(characters = listOf(armorKing))
        val expected = Character.Images(
            iconId = "T_UI_CS_Character_Thumb_Selected_knk.png",
            iconUrl = "https://tekkenwarehouse.com/wp-content/uploads/2025/10/T_UI_CS_Character_Thumb_Selected_knk.png",
        )

        //when
        val result = responseDto.toDomain(Game.Tekken8).single()

        //then
        assertThat(result.images).isEqualTo(expected)
    }

    @Test
    fun `no official large png means no icon`() {
        //given
        val responseDto = WavuCharacterListResponseDto(
            characters = listOf(
                armorKing.copy(
                    images = CharacterDto.Images(
                        largePng = "https://tekkenwarehouse.com/wp-content/uploads/2025/10/T_UI_CS_Character_Thumb_Selected_knk.png",
                    )
                )
            )
        )
        val expected = Character.Images()

        //when
        val result = responseDto.toDomain(Game.Tekken8).single()

        //then
        assertThat(result.images).isEqualTo(expected)
    }

    @Test
    fun `no images means no icon`() {
        //given
        val responseDto = WavuCharacterListResponseDto(characters = listOf(armorKing.copy(images = null)))

        //when
        val result = responseDto.toDomain(Game.Tekken8).single()

        //then
        assertThat(result.images?.iconUrl).isNull()
    }
    //endregion
}


private val armorKing = CharacterDto(
    id = "armor-king",
    displayName = "Armor King",
    wavuName = "Armor King",
    aliasList = listOf("ak", "aking", "armorking"),
    images = CharacterDto.Images(
        largePng = "https://tekkenwarehouse.com/wp-content/uploads/2025/10/T_UI_CS_Character_Thumb_Selected_knk.png",
        officialLargePng = "https://tekkenwarehouse.com/wp-content/uploads/2025/10/T_UI_CS_Character_Thumb_Selected_knk.png",
    ),
)

private val jack8 = CharacterDto(
    id = "jack-8",
    displayName = "Jack-8",
    wavuName = "Jack-8",
    aliasList = listOf("jack", "j8", "jack8"),
    images = CharacterDto.Images(
        largePng = "https://tekkenwarehouse.com/wp-content/uploads/2024/02/T_UI_CS_Character_Thumb_Selected_ccn.png",
        officialLargePng = "https://tekkenwarehouse.com/wp-content/uploads/2024/02/T_UI_CS_Character_Thumb_Selected_ccn.png",
    ),
)
