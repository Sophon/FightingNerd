package io.github.sophon.fightingnerd.adapter.inbound.move

import assertk.assertThat
import assertk.assertions.isEqualTo
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.move_list_char_bwd_dash_dist
import fightingnerd.composeapp.generated.resources.move_list_char_bwd_dash_speed
import fightingnerd.composeapp.generated.resources.move_list_char_bwd_walk_speed
import fightingnerd.composeapp.generated.resources.move_list_char_drush_max
import fightingnerd.composeapp.generated.resources.move_list_char_drush_min_block
import fightingnerd.composeapp.generated.resources.move_list_char_drush_min_throw
import fightingnerd.composeapp.generated.resources.move_list_char_fwd_dash_dist
import fightingnerd.composeapp.generated.resources.move_list_char_fwd_dash_speed
import fightingnerd.composeapp.generated.resources.move_list_char_fwd_walk_speed
import fightingnerd.composeapp.generated.resources.move_list_char_hp
import fightingnerd.composeapp.generated.resources.move_list_char_hp_life_points
import fightingnerd.composeapp.generated.resources.move_list_char_umo
import fightingnerd.composeapp.generated.resources.move_list_field_damage
import fightingnerd.composeapp.generated.resources.move_list_field_guard
import fightingnerd.composeapp.generated.resources.move_list_field_label_cancel
import fightingnerd.composeapp.generated.resources.move_list_field_label_chip
import fightingnerd.composeapp.generated.resources.move_list_field_label_invulnerability
import fightingnerd.composeapp.generated.resources.move_list_field_label_recovery
import fightingnerd.composeapp.generated.resources.move_list_field_on_block
import fightingnerd.composeapp.generated.resources.move_list_field_on_counter
import fightingnerd.composeapp.generated.resources.move_list_field_on_hit
import fightingnerd.composeapp.generated.resources.move_list_field_startup
import io.github.sophon.fightingnerd.adapter.inbound.move.model.Property
import io.github.sophon.fightingnerd.app.model.Character
import io.github.sophon.fightingnerd.app.model.Move
import io.github.sophon.fightingnerd.app.model.game.SF6MoveProperties
import io.github.sophon.fightingnerd.app.model.game.SFCharProperties
import io.github.sophon.fightingnerd.app.model.game.T8Properties
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test

internal class UiMappersTest {
    private val heatBurst = Move(
        input = "2+3",
        name = "Heat Burst",
        startup = "i16",
        guard = "m",
        damage = "12",
        onBlock = "+1",
        onHit = "+20a [Heat](https://wavu.wiki/t/Mechanics#Heat)",
        onCH = "+20a [Heat](https://wavu.wiki/t/Mechanics#Heat)",
        notes = listOf("Activates [Heat](https://wavu.wiki/t/Mechanics#Heat)", "Cancel into Heat Dash"),
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Kazuya_movelist#Kazuya-2+3"),
        gameProperties = T8Properties(isHeat = true, isPowerCrush = true),
        groupId = "Kazuya",
    )
    private val kazuyaThrow = Move(
        input = "1+3",
        startup = "i12",
        guard = "t",
        damage = "35",
        isThrow = true,
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Kazuya_movelist#Kazuya-1+3"),
        gameProperties = T8Properties(),
        groupId = "Kazuya",
    )
    private val shoryuken = Move(
        input = "623HP",
        name = "Heavy Shoryuken",
        startup = "6",
        guard = "LH",
        damage = "1400",
        onBlock = "-32",
        onHit = "KD +26",
        recovery = "29+14 land",
        cancel = "SA3",
        invulnerability = "1-6 Full",
        urls = Move.Urls(wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Ryu#Heavy_Shoryuken"),
        gameProperties = SF6MoveProperties(chip = "350"),
        groupId = "Shoryuken",
    )

    @Test
    fun `tekken properties become move properties`() {
        // given
        val expected = setOf(Property.Heat, Property.PowerCrush)

        // when
        val result = heatBurst.toUiMove().propertySet

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `throws are flagged`() {
        // given
        val expected = setOf(Property.Throw)

        // when
        val result = kazuyaThrow.toUiMove().propertySet

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `invulnerable moves are flagged`() {
        // given
        val expected = setOf(Property.Invincible)

        // when
        val result = shoryuken.toUiMove().propertySet

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `core fields lose their markdown links`() {
        // given
        val expected = persistentListOf(
            MoveListState.Field(Res.string.move_list_field_startup, "i16"),
            MoveListState.Field(Res.string.move_list_field_guard, "m"),
            MoveListState.Field(Res.string.move_list_field_damage, "12"),
            MoveListState.Field(Res.string.move_list_field_on_block, "+1"),
            MoveListState.Field(Res.string.move_list_field_on_hit, "+20a Heat"),
            MoveListState.Field(Res.string.move_list_field_on_counter, "+20a Heat"),
        )

        // when
        val result = heatBurst.toUiMove().coreFields

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `optional fields only list the values the move has`() {
        // given
        val expected = persistentListOf(
            MoveListState.Field(Res.string.move_list_field_label_recovery, "29+14 land"),
            MoveListState.Field(Res.string.move_list_field_label_cancel, "SA3"),
            MoveListState.Field(Res.string.move_list_field_label_invulnerability, "1-6 Full"),
            MoveListState.Field(Res.string.move_list_field_label_chip, "350"),
        )

        // when
        val result = shoryuken.toUiMove().optionalFields

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `notes lose their markdown links`() {
        // given
        val expected = persistentListOf("Activates Heat", "Cancel into Heat Dash")

        // when
        val result = heatBurst.toUiMove().notes

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `street fighter characters show their movement stats`() {
        // given
        val ryu = Character(
            id = "ryu",
            displayName = "Ryu",
            hp = "10000",
            gameProperties = SFCharProperties(
                fwdWalkSpd = "0.047",
                bwdWalkSpd = "0.032",
                fwdDashSpd = "19",
                bwdDashSpd = "23",
                fwdDashDist = "1.248",
                bwdDashDist = "0.876",
                dRushMin = "0.912",
                dRushBlock = "1.144",
                dRushMax = "2.962",
                throwRange = "0.8",
                throwHurtbox = "0.4",
                jumpSpd = "4+38+3",
                jumpApex = "2.15",
                fwdJumpDist = "1.9",
                bwdJumpDist = "1.52",
            ),
        )
        val expected = MoveListState.UiCharacter(
            displayName = "Ryu",
            propertyFields = persistentListOf(
                MoveListState.Field(Res.string.move_list_char_hp_life_points, "10000"),
                MoveListState.Field(Res.string.move_list_char_fwd_walk_speed, "0.047"),
                MoveListState.Field(Res.string.move_list_char_bwd_walk_speed, "0.032"),
                MoveListState.Field(Res.string.move_list_char_fwd_dash_speed, "19"),
                MoveListState.Field(Res.string.move_list_char_bwd_dash_speed, "23"),
                MoveListState.Field(Res.string.move_list_char_fwd_dash_dist, "1.248"),
                MoveListState.Field(Res.string.move_list_char_bwd_dash_dist, "0.876"),
                MoveListState.Field(Res.string.move_list_char_drush_min_throw, "0.912"),
                MoveListState.Field(Res.string.move_list_char_drush_min_block, "1.144"),
                MoveListState.Field(Res.string.move_list_char_drush_max, "2.962"),
            ),
        )

        // when
        val result = ryu.toUiCharacter()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `characters without game properties only show the hp they have`() {
        // given
        val kazuya = Character(id = "kazuya", displayName = "Kazuya", hp = "180")
        val expected = persistentListOf(MoveListState.Field(Res.string.move_list_char_hp, "180"))

        // when
        val result = kazuya.toUiCharacter().propertyFields

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `unique move entries are joined without their markdown links`() {
        // given
        val solBadguy = Character(
            id = "sol_badguy",
            displayName = "Sol Badguy",
            umo = listOf(
                "[Gun Flame](https://www.dustloop.com/w/GGST/Sol_Badguy#Gun_Flame)",
                "Wild Throw",
            ),
        )
        val expected = persistentListOf(MoveListState.Field(Res.string.move_list_char_umo, "Gun Flame, Wild Throw"))

        // when
        val result = solBadguy.toUiCharacter().propertyFields

        // then
        assertThat(result).isEqualTo(expected)
    }
}
