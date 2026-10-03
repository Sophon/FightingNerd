package io.github.sophon.discord.app.port.inbound

import io.github.sophon.discord.app.domain.model.AutocompleteChoice

interface ProduceAutoCompleteUseCase {
    suspend operator fun invoke(commandString: String, argument: String, query: String): List<AutocompleteChoice>
}
