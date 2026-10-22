package io.github.sophon.fightingnerd.inPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Question

/**
 * Without [characterId], every question picks a random character that has moves.
 */
interface GenerateQuestionsUseCase {
    suspend operator fun invoke(gameId: String, characterId: String?): Result<List<Question>, AppError>
}
