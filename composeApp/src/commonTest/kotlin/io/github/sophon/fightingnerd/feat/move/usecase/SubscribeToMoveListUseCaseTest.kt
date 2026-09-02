package io.github.sophon.fightingnerd.feat.move.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.FakeFeatureRepo
import io.github.sophon.fightingnerd.feat.FakeMediaRepo
import io.github.sophon.fightingnerd.feat.FakeWikiClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SubscribeToMoveListUseCaseTest {
    private val game = Game.Tekken8
    private val characterId = CharacterId("kazuya")
    private val kazuya = Character(
        id = "kazuya",
        displayName = "Kazuya",
        remoteQueryId = "kazuya",
        wikiUrl = "",
    )
    private val moves = listOf(
        Move(characterId = "kazuya", id = "1", input = "1", urls = Move.Urls(wikiUrl = "")),
        Move(characterId = "kazuya", id = "2", input = "2", urls = Move.Urls(wikiUrl = "")),
    )

    @Test
    fun `usecase emits empty flow when game id is invalid`() = runTest {
        // given
        val wikiClient = FakeWikiClient()
        val usecase = SubscribeToMoveListUseCase(
            featureRepo = FakeFeatureRepo(mapOf(game to wikiClient)),
            mediaRepo = FakeMediaRepo(),
        )
        val expected = emptyList<Result<Pair<Character, List<Move>>, AppError>>()

        // when
        val emissions = usecase("unknown-game", characterId).toList()

        //then
        assertThat(emissions).isEqualTo(expected)
    }

    @Test
    fun `usecase emits empty flow when no wiki client is registered for the game`() = runTest {
        // given
        val usecase = SubscribeToMoveListUseCase(
            featureRepo = FakeFeatureRepo(gameClients = emptyMap()),
            mediaRepo = FakeMediaRepo(),
        )
        val expected = emptyList<Result<Pair<Character, List<Move>>, AppError>>()

        // when
        val emissions = usecase(game.id, characterId).toList()

        //then
        assertThat(emissions).isEqualTo(expected)
    }

    @Test
    fun `usecase emits error when character is not in the character list`() = runTest {
        // given
        val wikiClient = FakeWikiClient(
            subscribeToCharacterListResult = emptyList(),
            subscribeToMoveListResult = moves,
        )
        val usecase = SubscribeToMoveListUseCase(
            featureRepo = FakeFeatureRepo(mapOf(game to wikiClient)),
            mediaRepo = FakeMediaRepo(),
        )
        val expected = AppError.WikiError("$characterId not found")

        // when
        val result = usecase(game.id, characterId).first()

        //then
        assertThat(result).isInstanceOf(Result.Error::class)
        val actualError = (result as? Result.Error)?.error
        assertThat(actualError).isEqualTo(expected)
    }

    @Test
    fun `usecase emits success with matching character and its move list`() = runTest {
        // given
        val wikiClient = FakeWikiClient(
            subscribeToCharacterListResult = listOf(kazuya),
            subscribeToMoveListResult = moves,
        )
        val usecase = SubscribeToMoveListUseCase(
            featureRepo = FakeFeatureRepo(mapOf(game to wikiClient)),
            mediaRepo = FakeMediaRepo(),
        )
        val expected = Pair(kazuya, moves)

        // when
        val result = usecase(game.id, characterId).first()

        //then
        assertThat(result).isInstanceOf(Result.Success::class)
        val actualData = (result as? Result.Success)?.data
        assertThat(actualData).isEqualTo(expected)
    }
}
