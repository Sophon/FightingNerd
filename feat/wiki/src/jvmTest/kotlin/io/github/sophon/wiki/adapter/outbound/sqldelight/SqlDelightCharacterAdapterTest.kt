package io.github.sophon.wiki.adapter.outbound.sqldelight

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.application.domain.model.Character
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SqlDelightCharacterAdapterTest {
    @Test
    fun `a saved character is loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(armorKing)

        // when
        database.save(Game.Tekken8, armorKing)

        // then
        val characterList = database.characterAdapter.subscribe(Game.Tekken8).first()
        assertThat(characterList).isEqualTo(expected)
    }

    @Test
    fun `saving a character again updates it`() = runTest {
        // given
        val database = TestWikiDatabase()
        val updatedArmorKing = armorKing.copy(
            images = Character.Images(iconUrl = "https://wavu.wiki/w/images/Armor_King_icon_2.png"),
        )
        val expected = listOf(updatedArmorKing)

        // when
        database.save(Game.Tekken8, armorKing)
        database.save(Game.Tekken8, updatedArmorKing)

        // then
        val characterList = database.characterAdapter.subscribe(Game.Tekken8).first()
        assertThat(characterList).isEqualTo(expected)
    }

    @Test
    fun `an alias taken in the game stays with the first character saved`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf("oscar", "oskar", "aska", "asuka")

        // when
        database.save(Game.Tekken8, jin)
        database.save(Game.Tekken8, asuka)

        // then
        val loadedAsuka = database.characterAdapter.subscribe(Game.Tekken8).first()
            .single { character -> character.id == asuka.id }
        assertThat(loadedAsuka.aliasList).isEqualTo(expected)
    }

    @Test
    fun `a character missing from four refreshes is kept`() = runTest {
        // given
        val database = TestWikiDatabase()
        database.save(Game.Tekken8, asuka)
        database.save(Game.Tekken8, jin)

        // when
        repeat(4) { database.characterAdapter.strike(Game.Tekken8, setOf(jin.id)) }

        // then
        val characterList = database.characterAdapter.subscribe(Game.Tekken8).first()
        assertThat(characterList).containsExactly(asuka, jin)
    }

    @Test
    fun `a character missing from five refreshes is deleted`() = runTest {
        // given
        val database = TestWikiDatabase()
        database.save(Game.Tekken8, asuka)
        database.save(Game.Tekken8, jin)

        // when
        repeat(5) { database.characterAdapter.strike(Game.Tekken8, setOf(jin.id)) }

        // then
        val characterList = database.characterAdapter.subscribe(Game.Tekken8).first()
        assertThat(characterList).containsExactly(jin)
    }

    @Test
    fun `saving a character clears its strikes`() = runTest {
        // given
        val database = TestWikiDatabase()
        database.save(Game.Tekken8, asuka)
        database.save(Game.Tekken8, jin)
        repeat(4) { database.characterAdapter.strike(Game.Tekken8, setOf(jin.id)) }

        // when
        database.save(Game.Tekken8, asuka)
        repeat(4) { database.characterAdapter.strike(Game.Tekken8, setOf(jin.id)) }

        // then
        val characterList = database.characterAdapter.subscribe(Game.Tekken8).first()
        assertThat(characterList).containsExactly(asuka, jin)
    }

    @Test
    fun `delete removes only that game's characters`() = runTest {
        // given
        val database = TestWikiDatabase()
        database.save(Game.Tekken8, jin)
        database.save(Game.GGST, solBadguy)

        // when
        database.characterAdapter.delete(Game.Tekken8)

        // then
        val tekkenCharacterList = database.characterAdapter.subscribe(Game.Tekken8).first()
        val ggstCharacterList = database.characterAdapter.subscribe(Game.GGST).first()
        assertThat(tekkenCharacterList).isEmpty()
        assertThat(ggstCharacterList).containsExactly(solBadguy)
    }

    @Test
    fun `deleting a character deletes its aliases, moves and game properties`() = runTest {
        // given
        val database = TestWikiDatabase()
        database.save(Game.Tekken8, jin, listOf(demonsPaw, windHookFist))
        val expected = 0L

        // when
        database.characterAdapter.delete(Game.Tekken8)

        // then
        assertThat(database.countRows("character_alias")).isEqualTo(expected)
        assertThat(database.countRows("move")).isEqualTo(expected)
        assertThat(database.countRows("move_alias")).isEqualTo(expected)
        assertThat(database.countRows("tekken8_move")).isEqualTo(expected)
    }
}
