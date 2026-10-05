package io.github.sophon.discord.inPort

import io.github.sophon.discord.app.model.AutocompleteChoice

interface ProduceAutoCompleteUseCase {
    suspend operator fun invoke(
        commandString: String,
        argument: String,
        query: String,
        argumentMap: Map<String, String>,
    ): List<AutocompleteChoice>
}
