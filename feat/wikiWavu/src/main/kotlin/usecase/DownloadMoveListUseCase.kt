package usecase

import cleanHtml
import cleanMoveInput
import com.example.core.domain.DataError
import com.example.core.domain.Result
import com.example.core.domain.map
import dataRemote.MoveDto
import dataRemote.MoveListResponseDto
import dataRemote.WavuWikiDataSource
import model.Character
import model.CharacterMoveList
import model.Move

internal class DownloadMoveListUseCase(
    private val source: WavuWikiDataSource,
) {
    suspend fun invoke(character: Character): Result<CharacterMoveList, DataError.Remote> {
        return source.fetchMoveList(character.name)
            .map { it.extractDto() }
            .map { moveList: List<MoveDto> ->
                moveList
                    .map { moveDto ->
                        Move(
                            charName = character.name,
                            id = moveDto.id
                                .substringAfter("-")
                                .cleanMoveInput(),
                            input = moveDto.input,
                            level = formCompleteDataFromParent(moveDto, moveList) { it.target },
                            name = moveDto.name,
                            parent = moveDto.parent,
                            damage = formCompleteDataFromParent(moveDto, moveList) { it.damage },
                            startup = getRootStartup(moveDto, moveList),
                            recoveryOnWhiff = moveDto.recv,
                            totalFrames =  moveDto.tot,
                            crush = moveDto.crush,
                            onBlock = moveDto.block,
                            onHit = moveDto.hit,
                            onCH = moveDto.ch,
                            notes = moveDto.splitNotes(),
                            alias = moveDto.alias,
                            image = moveDto.image,
                            videoId = moveDto.video,
                            alt = moveDto.alt,
                            isHeat = moveDto.isHE(),
                            isPowerCrush = moveDto.isPowerCrush(),
                            isHoming = moveDto.isHoming(),
                        )
                    }
            }
            .map { moveList: List<Move> ->
                CharacterMoveList(character, moveList)
            }
    }


    private fun MoveListResponseDto.extractDto(): List<MoveDto> = cargoQuery.map { it.title }

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
        moveDto: MoveDto,
        moveList: List<MoveDto>,
        fieldSelector: (MoveDto) -> String?,
    ): String? {
        var current: MoveDto? = moveDto
        val reverseLevel = mutableListOf<String>()

        while (current != null) {
            fieldSelector(current)?.let { reverseLevel.add(it) }
            current = current.parent?.let { parent ->
                moveList.firstOrNull { it.id == parent }
            }
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
        moveDto: MoveDto,
        moveList: List<MoveDto>,
    ): String? {
        var current: MoveDto? = moveDto
        var root: MoveDto = moveDto

        // Traverse up to find the topmost parent
        while (current != null) {
            root = current
            current = current.parent?.let { parent -> moveList.firstOrNull { it.id == parent } }
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