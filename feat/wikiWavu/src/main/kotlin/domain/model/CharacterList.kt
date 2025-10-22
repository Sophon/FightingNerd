package domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CharacterList(
    val characterList: List<Character>,
)
