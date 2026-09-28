package io.github.sophon.wiki.adapter.outbound.ktor

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.featureConfig.model.WikiClientFeature
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.adapter.outbound.ktor.dragDown.DragDownKtorGameDataAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.dreamCancel.DreamCancelKtorGameDataAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.dustLoop.DustLoopKtorGameDataAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.mizuumi.MizuumiKtorGameDataAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.superCombo.SuperComboKtorGameDataAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.wavu.WavuKtorGameDataAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.xko.XkoKtorGameDataAdapter
import io.github.sophon.wiki.application.port.outbound.FetchGameDataPort
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
            WikiClientFeature.Wavu -> wavuAdapter.fetch(game)
            WikiClientFeature.Mizuumi -> mizuumiAdapter.fetch(game)
            WikiClientFeature.DustLoop -> dustLoopAdapter.fetch(game)
            WikiClientFeature.SuperCombo -> superComboAdapter.fetch(game)
            WikiClientFeature.DragDown -> dragDownAdapter.fetch(game)
            WikiClientFeature.Xko -> xkoAdapter.fetch(game)
            WikiClientFeature.DreamCancel -> dreamCancelAdapter.fetch(game)
        }
        return flow
    }
}
