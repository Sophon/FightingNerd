package com.example.cornerman.screens.moveList.ui

import com.example.cornerman.screens.moveList.model.MoveCategory
import com.example.wikiwavu.domain.model.Move
import org.jetbrains.compose.resources.StringResource

data class MoveListViewState(
    val movesByCategory: List<MoveCategory> = emptyList(),
    val expandedNotesId: Set<String> = emptySet(),

    val error: StringResource? = null,
    val isLoading: Boolean = true,
) {
    companion object {
        val PREVIEW = MoveListViewState(
            movesByCategory = testMoves(),
            expandedNotesId = setOf("df2")
        )

        private fun testMoves(): List<MoveCategory> {
            return listOf(
                MoveCategory(
                    name = "Heat",
                    moves = listOf(
                        Move(
                            charName = "Dragunov",
                            id = "2+3",
                            input = "2+3",
                            level = "m",
                            startup = "i16",
                            onHit = "+1",
                            onBlock = "+2c",
                            notes = listOf(
                                "Heat Burst",
                                "100% recoverable damage",
                                "Chip damage on block",
                                "Partially uses remaining Heat time",
                                "Input b,b to cancel the attack"
                            )
                        ),
                        Move(
                            charName = "Dragunov",
                            id = "H.2+3",
                            input = "H.2+3",
                            level = "m,m,m",
                            startup = "i15~16,i30~32,i31~32",
                            onHit = "+3d",
                            onBlock = "+6",
                            notes = listOf(
                                "HeatSmash",
                                "Balcony Break",
                                "Only 1st and 3rd hit",
                                "7 Chip damage on block",
                                "Throw on 1st hit - 50 damage"
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
                        Move(
                            charName = "Dragunov",
                            id = "df1",
                            input = "df1",
                            level = "m",
                            startup = "i13",
                            onHit = "+7",
                            onBlock = "-2",
                            recoveryOnWhiff = "r23",
                            notes = listOf(
                                "Enter -2,-11 Pigeon Roll with 3+4 (or d3+4)"
                            )
                        ),
                        Move(
                            charName = "Dragunov",
                            id = "df14",
                            input = "df14",
                            level = "m,h",
                            startup = "i13",
                            onHit = "+22a (+13)",
                            onBlock = "-7",
                            onCH = "+58a",
                            notes = listOf(
                                "Balcony Break",
                                "Combo from 1st CH with 8f delay",
                                "Input can be delayed 12f"
                            )
                        ),
                        Move(
                            charName = "Dragunov",
                            id = "df2",
                            input = "df2",
                            level = "m",
                            startup = "i15",
                            onHit = "+28a (+18)",
                            onBlock = "-12",
                            notes = listOf(
                                "Launches crouching opponent"
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