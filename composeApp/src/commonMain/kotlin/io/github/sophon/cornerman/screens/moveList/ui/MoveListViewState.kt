package io.github.sophon.cornerman.screens.moveList.ui

import io.github.sophon.cornerman.screens.moveList.domain.MoveCategory
import io.github.sophon.cornerman.screens.moveList.domain.UiMove

data class MoveListViewState(
    val allMoves: List<MoveCategory> = emptyList(),
    val filteredMoves: List<MoveCategory> = emptyList(),
    val expandedNotesId: Set<String> = emptySet(),

    val searchBar: SearchBar? = null,

    val error: String? = null,
    val isLoading: Boolean = true,
) {
    data class SearchBar(
        val query: String = "",
        val type: Type,
    ) {
        enum class Type {
            CHIP,
            FIELD,
        }
    }

    companion object {
        val PREVIEW = MoveListViewState(
            allMoves = testMoves(),
            expandedNotesId = setOf("df2"),
            isLoading = false,
        )

        private fun testMoves(): List<MoveCategory> {
            return listOf(
                MoveCategory(
                    name = "Heat",
                    moves = listOf(
                        UiMove(
                            id = "2+3",
                            input = "2+3",
                            mandatoryFields = listOf(
                                UiMove.Field("Startup", "i16"),
                                UiMove.Field("OH", "+1"),
                                UiMove.Field("OB", "+2c"),
                                UiMove.Field("CH", null),
                                UiMove.Field("Level", "m"),
                            ),
                            optionalFields = listOf(
                                UiMove.Field("Damage", "12")
                            ),
                            notes = listOf(
                                "Heat Burst",
                                "100% recoverable damage",
                                "Chip damage on block",
                                "Partially uses remaining Heat time",
                                "Input b,b to cancel the attack"
                            )
                        ),
                        UiMove(
                            id = "H.2+3",
                            input = "H.2+3",
                            mandatoryFields = listOf(
                                UiMove.Field("Startup", "i15~16,i30~32,i31~32"),
                                UiMove.Field("OH", "+3d"),
                                UiMove.Field("OB", "+6"),
                                UiMove.Field("CH", null),
                            ),
                            optionalFields = listOf(
                                UiMove.Field("Damage", "25, 14, 23"),
                                UiMove.Field("Recovery", null),
                            ),
                            notes = listOf(
                                "Heat Smash",
                                "Balcony Break",
                                "Only 1st and 3rd hit",
                                "7 Chip damage on block",
                                "Throw on 1st hit - 50 damage",
                            )
                        )
                    )
                ),
                MoveCategory(
                    name = "n",
                    moves = listOf(),
                ),
                MoveCategory(
                    name = "df",
                    moves = listOf(
                        UiMove(
                            id = "df1",
                            input = "df1",
                            mandatoryFields = listOf(
                                UiMove.Field("Startup", "i13"),
                                UiMove.Field("OH", "+7"),
                                UiMove.Field("OB", "-2"),
                                UiMove.Field("CH", null),
                            ),
                            optionalFields = listOf(
                                UiMove.Field("Damage", "13"),
                                UiMove.Field("Recovery", "r23"),
                            ),
                            notes = listOf(
                                "Enter -2,-11 Pigeon Roll with 3+4 (or d3+4)",
                            )
                        ),
                        UiMove(
                            id = "df14",
                            input = "df14",
                            mandatoryFields = listOf(
                                UiMove.Field("Startup", "i13"),
                                UiMove.Field("OH", "+22a (+13)"),
                                UiMove.Field("OB", "+-7"),
                                UiMove.Field("CH", "+58a"),
                            ),
                            optionalFields = listOf(
                                UiMove.Field("Damage", "13,23"),
                                UiMove.Field("Recovery", "r27"),
                            ),
                            notes = listOf(
                                "Balcony Break",
                                "Combo from 1st CH with 8f delay",
                                "Input can be delayed 12f"
                            )
                        ),
                        UiMove(
                            id = "df2",
                            input = "df2",
                            mandatoryFields = listOf(
                                UiMove.Field("Startup", "i15~16"),
                                UiMove.Field("OH", "+28a"),
                                UiMove.Field("OB", "-12"),
                                UiMove.Field("CH", null),
                            ),
                            optionalFields = listOf(
                                UiMove.Field("Damage", "14"),
                                UiMove.Field("Recovery", "r31"),
                            ),
                            notes = listOf(
                                "Launches crouching opponent",
                            )
                        ),
                    )
                ),
                MoveCategory(
                    name = "d",
                    moves = listOf(),
                ),
                MoveCategory(
                    name = "db",
                    moves = listOf(),
                ),
                MoveCategory(
                    name = "b",
                    moves = listOf(),
                ),
            )
        }
    }
}