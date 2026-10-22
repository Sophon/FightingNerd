package io.github.sophon.wiki.adapter.outbound.sqldelight.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.game.SFCharProperties
import io.github.sophon.wiki.model.wiki.Game
import kotlin.test.Test
import io.github.sophon.wiki.data.Character as CharacterEntity

class SqlDelightCharacterMapperTest {

    //region id
    @Test
    fun `id is the selecting game and the natural id`() {
        //given
        val entity = ryuEntity
        val expected = CharacterId(Game.StreetFighter6, "ryu")

        //when
        val result = entity.toDomain(Game.StreetFighter6, aliasList = emptyList(), gameProperties = null)

        //then
        assertThat(result.id).isEqualTo(expected)
    }
    //endregion

    //region images
    @Test
    fun `image columns are grouped into images`() {
        //given
        val entity = ryuEntity
        val expected = Character.Images(
            iconId = "SF6_Ryu_Icon.png",
            iconUrl = "https://wiki.supercombo.gg/images/SF6_Ryu_Icon.png",
            bannerUrl = "https://wiki.supercombo.gg/images/SF6_Ryu_Banner.png",
        )

        //when
        val result = entity.toDomain(Game.StreetFighter6, aliasList = emptyList(), gameProperties = null)

        //then
        assertThat(result.images).isEqualTo(expected)
    }

    @Test
    fun `one image column is enough to keep images`() {
        //given
        val entity = ryuEntity.copy(icon_id = null, icon_url = null)
        val expected = Character.Images(bannerUrl = "https://wiki.supercombo.gg/images/SF6_Ryu_Banner.png")

        //when
        val result = entity.toDomain(Game.StreetFighter6, aliasList = emptyList(), gameProperties = null)

        //then
        assertThat(result.images).isEqualTo(expected)
    }

    @Test
    fun `no image columns means no images`() {
        //given
        val entity = ryuEntity.copy(icon_id = null, icon_url = null, banner_url = null)

        //when
        val result = entity.toDomain(Game.StreetFighter6, aliasList = emptyList(), gameProperties = null)

        //then
        assertThat(result.images).isNull()
    }
    //endregion

    //region full row
    @Test
    fun `every column, alias and game property is mapped`() {
        //given
        val entity = ryuEntity
        val aliasList = listOf("ryu", "ryuu")
        val expected = Character(
            id = CharacterId(Game.StreetFighter6, "ryu"),
            displayName = "Ryu",
            remoteQueryId = "Ryu",
            wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Ryu",
            aliasList = aliasList,
            images = Character.Images(
                iconId = "SF6_Ryu_Icon.png",
                iconUrl = "https://wiki.supercombo.gg/images/SF6_Ryu_Icon.png",
                bannerUrl = "https://wiki.supercombo.gg/images/SF6_Ryu_Banner.png",
            ),
            hp = "10000",
            umo = listOf("Denjin Charge"),
            gameProperties = ryuProperties,
        )

        //when
        val result = entity.toDomain(Game.StreetFighter6, aliasList, ryuProperties)

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}


private val ryuEntity = CharacterEntity(
    id = 1,
    game = Game.StreetFighter6.id,
    natural_id = "ryu",
    remote_query_id = "Ryu",
    display_name = "Ryu",
    wiki_url = "https://wiki.supercombo.gg/w/Street_Fighter_6/Ryu",
    icon_id = "SF6_Ryu_Icon.png",
    icon_url = "https://wiki.supercombo.gg/images/SF6_Ryu_Icon.png",
    banner_url = "https://wiki.supercombo.gg/images/SF6_Ryu_Banner.png",
    hp = "10000",
    umo = listOf("Denjin Charge"),
    strike_count = 0,
    updated_at = 1_759_700_000_000,
)

private val ryuProperties = SFCharProperties(
    fwdWalkSpd = "0.047",
    bwdWalkSpd = "0.032",
    fwdDashSpd = "19",
    bwdDashSpd = "23",
    fwdDashDist = "1.293",
    bwdDashDist = "0.92",
    dRushMin = "11",
    dRushBlock = "+4",
    dRushMax = "3.025",
    throwRange = "0.8",
    throwHurtbox = "0.2",
    jumpSpd = "4+38+3",
    jumpApex = "2.064",
    fwdJumpDist = "1.92",
    bwdJumpDist = "1.52",
)
