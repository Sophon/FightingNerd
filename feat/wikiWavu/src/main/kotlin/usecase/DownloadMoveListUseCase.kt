package usecase

import WavuError
import cleanHtml
import cleanMoveInput
import com.example.core.domain.Result
import dataRemote.MoveDto
import dataRemote.MoveListResponseDto
import dataRemote.WavuWikiDataSource
import model.Character
import model.CharacterMoveList
import model.Move

internal class DownloadMoveListUseCase(
    private val source: WavuWikiDataSource,
) {
    suspend fun invoke(character: Character): Result<CharacterMoveList, WavuError> {
        return when (val result = source.fetchMoveList(character.name)) {
            is Result.Success -> {
                val downloadedMoves: List<MoveDto> = result.data.extractDto()
                val movesById = downloadedMoves.associateBy { it.id }

                val moveList: List<Move> = downloadedMoves
                    .map { it.mapToDomain(character, movesById) }

                Result.Success(CharacterMoveList(character, moveList))
            }
            is Result.Error -> {
                Result.Error(WavuError.DOWNLOAD_ERROR)
            }
        }
    }


    private fun MoveListResponseDto.extractDto(): List<MoveDto> = cargoQuery.map { it.title }

    private fun MoveDto.mapToDomain(
        character: Character,
        movesById: Map<String, MoveDto>,
    ): Move {
        return Move(
            charName = character.name,
            id = id
                .substringAfter("-")
                .cleanMoveInput(),
            input = input,
            level = formCompleteDataFromParent(movesById) { it.target },
            name = name,
            parent = parent,
            damage = formCompleteDataFromParent(movesById) { it.damage },
            startup = getRootStartup(movesById),
            recoveryOnWhiff = recv,
            totalFrames =  tot,
            crush = crush,
            onBlock = block,
            onHit = hit,
            onCH = ch,
            notes = splitNotes(),
            alias = alias,
            image = image,
            videoId = video,
            alt = alt,
            isHeat = isHE(),
            isPowerCrush = isPowerCrush(),
            isHoming = isHoming(),
        )
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
    private fun MoveDto.formCompleteDataFromParent(
        movesById: Map<String, MoveDto>,
        fieldSelector: (MoveDto) -> String?,
    ): String? {
        var current: MoveDto? = this
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
    private fun MoveDto.getRootStartup(
        movesById: Map<String, MoveDto>
    ): String? {
        var current: MoveDto? = this
        var root: MoveDto = this

        // Traverse up to find the topmost parent
        while (current != null) {
            root = current
            current = current.parent?.let { movesById[it] }
        }

        return root.startup
    }

    private fun MoveDto.splitNotes(): List<String> {
        val finalNotes = notes.orEmpty()
            .trimIndent()
            .cleanHtml()
            .replace("\n\n", "\n")
            .lines()
            .filter { it.isNotEmpty() }
            .map { it.removePrefix("* ").trim() }

        return finalNotes
    }

    private fun MoveDto.isHE(): Boolean {
        return notes?.contains("Heat Engager", ignoreCase = true) == true
    }

    private fun MoveDto.isPowerCrush(): Boolean {
        return (crush?.contains("pc", ignoreCase = true) == true)
    }

    private fun MoveDto.isHoming(): Boolean {
        return notes?.contains("Homing", ignoreCase = true) == true
    }
}