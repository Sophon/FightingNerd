package io.github.sophon.wiki.adapter.outbound.sqldelight.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.game.T8Properties
import kotlin.test.Test
import io.github.sophon.wiki.data.Move as MoveEntity

class SqlDelightMoveMapperTest {

    //region urls
    @Test
    fun `url and media columns are grouped into urls`() {
        //given
        val entity = jinOneTwoEntity
        val expected = Move.Urls(
            wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-1,2",
            videoId = "Jin_1,2.mp4",
            videoUrl = "https://wavu.wiki/images/Jin_1,2.mp4",
            hitboxImageList = listOf("https://wavu.wiki/images/Jin_1,2_hitbox.png"),
            moveImageList = listOf("https://wavu.wiki/images/Jin_1,2.png"),
        )

        //when
        val result = entity.toDomain(aliases = emptyList(), gameProperties = null)

        //then
        assertThat(result.urls).isEqualTo(expected)
    }
    //endregion

    //region sparse row
    @Test
    fun `a row with only required columns has no optional data`() {
        //given
        val entity = jinOneTwoEntity.copy(
            remote_id = null,
            name = null,
            damage = null,
            startup = null,
            on_block = null,
            on_hit = null,
            on_ch = null,
            active = null,
            cancel = null,
            recovery = null,
            guard = null,
            invulnerability = null,
            type = null,
            notes = emptyList(),
            video_id = null,
            video_url = null,
            hitbox_image_list = emptyList(),
            move_image_list = emptyList(),
        )
        val expected = Move(
            input = "1,2",
            urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-1,2"),
        )

        //when
        val result = entity.toDomain(aliases = emptyList(), gameProperties = null)

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region full row
    @Test
    fun `every column, alias and game property is mapped`() {
        //given
        val entity = jinOneTwoEntity
        val aliases = listOf("Jab, Cross Straight")
        val gameProperties = T8Properties(hasWallInteraction = true)
        val expected = Move(
            input = "1,2",
            remoteId = "Jin-1,2",
            name = "Jab, Cross Straight",
            damage = "5,12",
            startup = "i10",
            onBlock = "+1",
            onHit = "+8",
            onCH = "+8",
            active = "1",
            cancel = "r22",
            recovery = "r22",
            guard = "h,h",
            invulnerability = "",
            isThrow = false,
            type = "string",
            notes = listOf("Combo from 1st hit"),
            aliases = aliases,
            urls = Move.Urls(
                wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-1,2",
                videoId = "Jin_1,2.mp4",
                videoUrl = "https://wavu.wiki/images/Jin_1,2.mp4",
                hitboxImageList = listOf("https://wavu.wiki/images/Jin_1,2_hitbox.png"),
                moveImageList = listOf("https://wavu.wiki/images/Jin_1,2.png"),
            ),
            gameProperties = gameProperties,
        )

        //when
        val result = entity.toDomain(aliases, gameProperties)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `throw flag is mapped`() {
        //given
        val entity = jinOneTwoEntity.copy(input = "1+3", is_throw = true)
        val expected = true

        //when
        val result = entity.toDomain(aliases = emptyList(), gameProperties = null)

        //then
        assertThat(result.isThrow).isEqualTo(expected)
    }
    //endregion
}


private val jinOneTwoEntity = MoveEntity(
    id = 1,
    character_id = 1,
    input = "1,2",
    remote_id = "Jin-1,2",
    position = 0,
    name = "Jab, Cross Straight",
    damage = "5,12",
    startup = "i10",
    on_block = "+1",
    on_hit = "+8",
    on_ch = "+8",
    active = "1",
    cancel = "r22",
    recovery = "r22",
    guard = "h,h",
    invulnerability = "",
    type = "string",
    is_throw = false,
    notes = listOf("Combo from 1st hit"),
    wiki_url = "https://wavu.wiki/t/Jin_movelist#Jin-1,2",
    video_id = "Jin_1,2.mp4",
    video_url = "https://wavu.wiki/images/Jin_1,2.mp4",
    hitbox_image_list = listOf("https://wavu.wiki/images/Jin_1,2_hitbox.png"),
    move_image_list = listOf("https://wavu.wiki/images/Jin_1,2.png"),
    strike_count = 0,
)
