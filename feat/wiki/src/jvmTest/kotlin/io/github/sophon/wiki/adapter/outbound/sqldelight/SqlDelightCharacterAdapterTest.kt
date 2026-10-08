package io.github.sophon.wiki.adapter.outbound.sqldelight

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.wiki.Game
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
        database.save(armorKing)

        // then
        val characterList = database.characterAdapter.subscribe(Game.Tekken8).first()
        assertThat(characterList).isEqualTo(expected)
    }

    @Test
    fun `a saved character is loaded by its ID with aliases`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = armorKing

        // when
        database.save(jin)
        database.save(armorKing)

        // then
        val character = database.characterAdapter.get(armorKing.id)
        assertThat(character).isEqualTo(expected)
    }

    @Test
    fun `an unknown character loads nothing`() = runTest {
        // given
        val database = TestWikiDatabase()

        // when
        database.save(jin)

        // then
        val character = database.characterAdapter.get(armorKing.id)
        assertThat(character).isNull()
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
        database.save(armorKing)
        database.save(updatedArmorKing)

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
        database.save(jin)
        database.save(asuka)

        // then
        val loadedAsuka = database.characterAdapter.subscribe(Game.Tekken8).first()
            .single { character -> character.id == asuka.id }
        assertThat(loadedAsuka.aliasList).isEqualTo(expected)
    }

    @Test
    fun `a character missing from four refreshes is kept`() = runTest {
        // given
        val database = TestWikiDatabase()
        database.save(asuka)
        database.save(jin)

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
        database.save(asuka)
        database.save(jin)

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
        database.save(asuka)
        database.save(jin)
        repeat(4) { database.characterAdapter.strike(Game.Tekken8, setOf(jin.id)) }

        // when
        database.save(asuka)
        repeat(4) { database.characterAdapter.strike(Game.Tekken8, setOf(jin.id)) }

        // then
        val characterList = database.characterAdapter.subscribe(Game.Tekken8).first()
        assertThat(characterList).containsExactly(asuka, jin)
    }

    @Test
    fun `delete removes only that game's characters`() = runTest {
        // given
        val database = TestWikiDatabase()
        database.save(jin)
        database.save(solBadguy)

        // when
        database.characterAdapter.delete(Game.Tekken8)

        // then
        val tekkenCharacterList = database.characterAdapter.subscribe(Game.Tekken8).first()
        val ggstCharacterList = database.characterAdapter.subscribe(Game.GGST).first()
        assertThat(tekkenCharacterList).isEmpty()
        assertThat(ggstCharacterList).containsExactly(solBadguy)
    }

    @Test
    fun `a character saved without a move list has no moves`() = runTest {
        // given
        val database = TestWikiDatabase()

        // when
        database.characterAdapter.saveCharacterList(listOf(jin, armorKing))

        // then
        val moveList = database.moveAdapter.subscribe(jin.id).first()
        assertThat(moveList).isEmpty()
    }

    @Test
    fun `a move list of an unsaved character fails`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = Result.Error(DataError.Local.UNKNOWN)

        // when
        val result = database.characterAdapter.saveMoveList(jin.id, listOf(demonsPaw))

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `game data saves every character with its moves`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(demonsPaw, windHookFist)

        // when
        database.characterAdapter.saveGameData(listOf(jin to expected, armorKing to emptyList()))

        // then
        val characterList = database.characterAdapter.subscribe(Game.Tekken8).first()
        assertThat(characterList.map { character -> character.id }).containsExactly(armorKing.id, jin.id)
        val moveList = database.moveAdapter.subscribe(jin.id).first()
        assertThat(moveList).isEqualTo(expected)
    }

    @Test
    fun `deleting a character deletes its aliases, moves and game properties`() = runTest {
        // given
        val database = TestWikiDatabase()
        database.save(jin, listOf(demonsPaw, windHookFist))
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
