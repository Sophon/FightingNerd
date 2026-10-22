package io.github.sophon.wiki.adapter.outbound.ktor

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.adapter.outbound.ktor.dragDown.DragDownKtorGameDataAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.dreamCancel.DreamCancelKtorGameDataAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.dustLoop.DustLoopKtorGameDataAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.mizuumi.MizuumiKtorGameDataAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.superCombo.SuperComboKtorGameDataAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.wavu.WavuKtorGameDataAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.xko.XkoKtorGameDataAdapter
import io.github.sophon.wiki.app.outPort.FetchGameDataPort
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.wiki.Game
import io.github.sophon.wiki.model.wiki.Wiki

/**
 * Routes each game to its wiki's adapter - which wiki serves which game stays out of the service.
 */
internal class KtorGameDataAdapter(
    private val wavuAdapter: WavuKtorGameDataAdapter,
    private val mizuumiAdapter: MizuumiKtorGameDataAdapter,
    private val dustLoopAdapter: DustLoopKtorGameDataAdapter,
    private val superComboAdapter: SuperComboKtorGameDataAdapter,
    private val dragDownAdapter: DragDownKtorGameDataAdapter,
    private val xkoAdapter: XkoKtorGameDataAdapter,
    private val dreamCancelAdapter: DreamCancelKtorGameDataAdapter,
) : FetchGameDataPort {
    override suspend fun fetchCharacterList(
        game: Game,
    ): Result<List<Character>, DataError.Remote> {
        val result = adapterOf(game).fetchCharacterList(game)
        return result
    }

    override suspend fun fetchMoveList(
        character: Character,
    ): Result<List<Move>, DataError.Remote> {
        val result = adapterOf(character.id.game).fetchMoveList(character)
        return result
    }

    override suspend fun fetchGameData(
        game: Game,
    ): Result<List<Pair<Character, List<Move>>>, DataError.Remote> {
        val result = adapterOf(game).fetchGameData(game)
        return result
    }

    private fun adapterOf(game: Game): FetchGameDataPort {
        val adapter = when (game.wiki) {
            Wiki.Wavu -> wavuAdapter
            Wiki.Mizuumi -> mizuumiAdapter
            Wiki.DustLoop -> dustLoopAdapter
            Wiki.SuperCombo -> superComboAdapter
            Wiki.DragDown -> dragDownAdapter
            Wiki.Xko -> xkoAdapter
            Wiki.DreamCancel -> dreamCancelAdapter
        }
        return adapter
    }
}
