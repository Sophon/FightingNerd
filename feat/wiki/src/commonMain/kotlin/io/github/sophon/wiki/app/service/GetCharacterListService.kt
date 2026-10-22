package io.github.sophon.wiki.app.service

import io.github.sophon.wiki.GetCharacterListUseCase
import io.github.sophon.wiki.app.outPort.LoadCharacterListPort
import io.github.sophon.wiki.app.outPort.LoadWikiConfigPort
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
internal class GetCharacterListService(
    private val loadWikiConfigPort: LoadWikiConfigPort,
    private val loadCharacterListPort: LoadCharacterListPort,
) : GetCharacterListUseCase {
    override fun invoke(): Flow<List<Character>> {
        val flow = loadWikiConfigPort
            .subscribe()
            .filterNotNull()
            .map { wikiConfig -> wikiConfig.enabledGameSet }
            .distinctUntilChanged()
            .flatMapLatest { gameSet -> subscribeToCharacterList(gameSet) }
        return flow
    }

    /**
     * One list in [gameSet] order - `combine` of no flows never emits, so an empty set is an empty list.
     */
    private fun subscribeToCharacterList(gameSet: Set<Game>): Flow<List<Character>> {
        val flow = if (gameSet.isEmpty()) {
            flowOf(emptyList())
        } else {
            val characterListFlowList = gameSet.map { game -> loadCharacterListPort.subscribe(game) }
            combine(characterListFlowList) { characterListArray ->
                val combinedList = characterListArray.toList().flatten()
                combinedList
            }
        }
        return flow
    }
}
