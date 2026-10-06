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
import kotlinx.coroutines.flow.Flow

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
    override fun fetch(
        game: Game,
    ): Flow<Result<Pair<Character, List<Move>>, DataError.Remote>> {
        val flow = when (game.wiki) {
            Wiki.Wavu -> wavuAdapter.fetch(game)
            Wiki.Mizuumi -> mizuumiAdapter.fetch(game)
            Wiki.DustLoop -> dustLoopAdapter.fetch(game)
            Wiki.SuperCombo -> superComboAdapter.fetch(game)
            Wiki.DragDown -> dragDownAdapter.fetch(game)
            Wiki.Xko -> xkoAdapter.fetch(game)
            Wiki.DreamCancel -> dreamCancelAdapter.fetch(game)
        }
        return flow
    }
}
