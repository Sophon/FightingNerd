package io.github.sophon.wiki.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.WikiError
import io.github.sophon.wiki.model.wiki.Game
import io.github.sophon.wiki.app.outPort.LoadCharacterPort
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class GetCharacterServiceTest {
    @Test
    fun `a stored character is returned`() = runTest {
        // given
        val service = _root_ide_package_.io.github.sophon.wiki.app.service.GetCharacterService(FakeLoadCharacterPort(jin))
        val expected = Result.Success(jin)

        // when
        val result = service(jin.id)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `a missing character is an unknown character`() = runTest {
        // given
        val service = _root_ide_package_.io.github.sophon.wiki.app.service.GetCharacterService(FakeLoadCharacterPort())
        val expected = listOf("jin")

        // when
        val result = service(jin.id)

        // then
        assertThat(result)
            .isInstanceOf(Result.Error::class)
            .prop(Result.Error<*>::error)
            .isInstanceOf(WikiError.UnknownCharacter::class)
            .transform { error -> error.inputs.toList() }
            .isEqualTo(expected)
    }
}


private val jin = Character(
    id = CharacterId(Game.Tekken8, "jin"),
    displayName = "Jin",
    remoteQueryId = "Jin",
    wikiUrl = "https://wavu.wiki/t/Jin",
)

private class FakeLoadCharacterPort(vararg storedCharacters: Character) : LoadCharacterPort {
    private val storedCharacterList = storedCharacters.toList()

    override suspend fun get(characterId: CharacterId): Character? {
        val character = storedCharacterList.firstOrNull { character -> character.id == characterId }
        return character
    }
}
