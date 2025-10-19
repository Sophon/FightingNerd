package usecase

import cleanHtml
import cleanMoveInput
import com.example.core.domain.DataError
import com.example.core.domain.Result
import com.example.core.domain.map
import dataRemote.MoveDto
import dataRemote.WavuWikiDataSource
import model.Move

internal class DownloadMoveListUseCase(
    private val source: WavuWikiDataSource,
) {
    suspend fun invoke(charName: String): Result<Map<String, Move>, DataError.Remote> {
        return source.fetchMoveList(charName)
            .map { dto -> dto.cargoQuery.map { it.title } }
            .map { moves ->
                val movesById = moves
                    .map { move ->
                        move.copy(
                            notes = move.notes
                                ?.cleanHtml()
                                ?.replace("\n\n", "\n"),
                        )
                    }
                    .associateBy { it.id }

                movesById
                    .mapValues { (_, move) ->
                        Move(
                            id = move.id.substringAfter("-"),
                            input = move.input,
                            level = formCompleteDataFromParent(move, movesById) {
                                it.target
                            },
                            name = move.name,
                            parent = move.parent,
                            damage = formCompleteDataFromParent(move, movesById) {
                                it.damage
                            },
                            startup = getRootStartup(move, movesById),
                            recoveryOnWhiff = move.recv,
                            totalFrames = move.tot,
                            crush = move.crush,
                            onBlock = move.block,
                            onHit = move.hit,
                            onCH = move.ch,
                            notes = move.splitNotes(),
                            alias = move.alias,
                            image = move.image,
                            videoId = move.video,
                            alt = move.alt,
                            isHeatEngager = move.isHE(),
                            isPowerCrush = move.isPowerCrush(),
                        )
                    }
                    .mapKeys { (id, _) ->
                        id
                            .substringAfter("-")
                            .cleanMoveInput()
                    }
            }
    }

    /**
     * Kazuya's 112 is actually:
     *
     *  - input: ,2
     *  - damage: ,6
     *  - parent: Kazuya-1,1,
     *
     *  So we have to traverse through parents to form the complete string
     */
    private fun formCompleteDataFromParent(
        move: MoveDto,
        movesById: Map<String, MoveDto>,
        fieldSelector: (MoveDto) -> String?,
    ): String? {
        var current: MoveDto? = move
        val reverseLevel = mutableListOf<String>()

        while (current != null) {
            fieldSelector(current)?.let { reverseLevel.add(it) }
            current = current.parent?.let { parent -> movesById[parent] }
        }

        return reverseLevel
            .reversed()
            .joinToString("")
            .takeIf { it.isNotEmpty() }
    }

    /**
     * Similar to the issue above, just with startup
     */
    private fun getRootStartup(
        move: MoveDto,
        movesById: Map<String, MoveDto>
    ): String? {
        var current: MoveDto? = move
        var root: MoveDto = move

        // Traverse up to find the topmost parent
        while (current != null) {
            root = current
            current = current.parent?.let { movesById[it] }
        }

        return root.startup
    }

    private fun MoveDto.isHE(): Boolean {
        return notes?.contains("Heat", ignoreCase = true) == true
    }

    private fun MoveDto.isPowerCrush(): Boolean {
        return (crush?.contains("pc", ignoreCase = true) == true)
    }

    private fun MoveDto.splitNotes(): List<String> {
        val finalNotes = notes.orEmpty()
            .trimIndent()
            .lines()
            .filter { it.isNotEmpty() }
            .map { it.removePrefix("* ").trim() }

        return finalNotes
    }
}