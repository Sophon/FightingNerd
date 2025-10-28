package com.example.cornerman.moveList.mapper

import assertk.assertThat
import assertk.assertions.*
import com.example.wikiwavu.domain.model.Move
import kotlin.test.Test

class MapperTest {
    //region isDirectional() tests
    @Test
    fun `isDirectional returns df for df+1 input`() {
        // given
        val move = createMove("df+1")

        // when
        val result = move.isDirectional()

        // then
        assertThat(result).isEqualTo("df")
    }

    @Test
    fun `isDirectional returns db for db+1 input`() {
        // given
        val move = createMove("db+1")

        // when
        val result = move.isDirectional()

        // then
        assertThat(result).isEqualTo("db")
    }

    @Test
    fun `isDirectional returns f for f+2 input`() {
        // given
        val move = createMove("f+2")

        // when
        val result = move.isDirectional()

        // then
        assertThat(result).isEqualTo("f")
    }

    @Test
    fun `isDirectional returns d for d+1 input`() {
        // given
        val move = createMove("d+1")

        // when
        val result = move.isDirectional()

        // then
        assertThat(result).isEqualTo("d")
    }

    @Test
    fun `isDirectional returns b for b+1 input`() {
        // given
        val move = createMove("b+1")

        // when
        val result = move.isDirectional()

        // then
        assertThat(result).isEqualTo("b")
    }

    @Test
    fun `isDirectional returns null for neutral input 1`() {
        // given
        val move = createMove("1")

        // when
        val result = move.isDirectional()

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `isDirectional returns null for stance input DGF dot 1`() {
        // given
        val move = createMove("DGF.1")

        // when
        val result = move.isDirectional()

        // then
        assertThat(result).isNull()
    }
    //endregion

    //region isMotion() tests
    @Test
    fun `isMotion returns true for f comma F plus 1 plus 2 input`() {
        // given
        val move = createMove("f,F+1+2")

        // when
        val result = move.isMotion()

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `isMotion returns true for qcf plus 1 input`() {
        // given
        val move = createMove("qcf+1")

        // when
        val result = move.isMotion()

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `isMotion returns false for df plus 1 input`() {
        // given
        val move = createMove("df+1")

        // when
        val result = move.isMotion()

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `isMotion returns false for neutral input 1`() {
        // given
        val move = createMove("1")

        // when
        val result = move.isMotion()

        // then
        assertThat(result).isFalse()
    }
    //endregion

    //region isCrouch() tests
    @Test
    fun `isCrouch returns true for FC dot d plus 1 input`() {
        // given
        val move = createMove("FC.d+1")

        // when
        val result = move.isCrouch()

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `isCrouch returns true for FC dot df plus 1 input`() {
        // given
        val move = createMove("FC.df+1")

        // when
        val result = move.isCrouch()

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `isCrouch returns false for d plus 1 input`() {
        // given
        val move = createMove("d+1")

        // when
        val result = move.isCrouch()

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `isCrouch returns false for neutral input 1`() {
        // given
        val move = createMove("1")

        // when
        val result = move.isCrouch()

        // then
        assertThat(result).isFalse()
    }
    //endregion

    //region isWS() tests
    @Test
    fun `isWS returns true for ws1 input`() {
        // given
        val move = createMove("ws1")

        // when
        val result = move.isWS()

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `isWS returns true for ws1 plus 4 input`() {
        // given
        val move = createMove("ws1+4")

        // when
        val result = move.isWS()

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `isWS returns false for neutral input 1`() {
        // given
        val move = createMove("1")

        // when
        val result = move.isWS()

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `isWS returns false for FC dot d plus 1 input`() {
        // given
        val move = createMove("FC.d+1")

        // when
        val result = move.isWS()

        // then
        assertThat(result).isFalse()
    }
    //endregion

    //region isCD() tests
    @Test
    fun `isCD returns true for CD plus 1 input`() {
        // given
        val move = createMove("CD+1")

        // when
        val result = move.isCD()

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `isCD returns false for f comma n comma d comma df plus 1 input`() {
        // given
        val move = createMove("f,n,d,df+1")

        // when
        val result = move.isCD()

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `isCD returns false for neutral input 1`() {
        // given
        val move = createMove("1")

        // when
        val result = move.isCD()

        // then
        assertThat(result).isFalse()
    }
    //endregion

    //region isBT() tests
    @Test
    fun `isBT returns true for BT dot 1 input`() {
        // given
        val move = createMove("BT.1")

        // when
        val result = move.isBT()

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `isBT returns true for BT dot d plus 1 input`() {
        // given
        val move = createMove("BT.d+1")

        // when
        val result = move.isBT()

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `isBT returns false for neutral input 1`() {
        // given
        val move = createMove("1")

        // when
        val result = move.isBT()

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `isBT returns false for DGF dot 1 input`() {
        // given
        val move = createMove("DGF.1")

        // when
        val result = move.isBT()

        // then
        assertThat(result).isFalse()
    }
    //endregion

    //region isThrow() tests
    @Test
    fun `isThrow returns true when notes contain Throw break`() {
        // given
        val move = createMove("1+3", notes = listOf("Homing", "Throw break 1 or 2"))

        // when
        val result = move.isThrow()

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `isThrow returns true when notes contain throw in lowercase`() {
        // given
        val move = createMove("2+4", notes = listOf("Homing", "Floor Break", "Throw break 1 or 2", "Side switch on hit"))

        // when
        val result = move.isThrow()

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `isThrow returns false when notes do not contain throw`() {
        // given
        val move = createMove("1", notes = listOf("Recovers 2f faster on hit or block (t27 r17)"))

        // when
        val result = move.isThrow()

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `isThrow returns false when notes are empty`() {
        // given
        val move = createMove("1", notes = emptyList())

        // when
        val result = move.isThrow()

        // then
        assertThat(result).isFalse()
    }
    //endregion

    //region isNeutralInput() tests
    @Test
    fun `isNeutralInput returns true for input 1`() {
        // given
        val move = createMove("1")

        // when
        val result = move.isNeutralInput()

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `isNeutralInput returns true for input 2`() {
        // given
        val move = createMove("2")

        // when
        val result = move.isNeutralInput()

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `isNeutralInput returns true for input 3`() {
        // given
        val move = createMove("3")

        // when
        val result = move.isNeutralInput()

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `isNeutralInput returns true for input 4`() {
        // given
        val move = createMove("4")

        // when
        val result = move.isNeutralInput()

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `isNeutralInput returns false for input f plus 2`() {
        // given
        val move = createMove("f+2")

        // when
        val result = move.isNeutralInput()

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `isNeutralInput returns false for input DGF dot 1`() {
        // given
        val move = createMove("DGF.1")

        // when
        val result = move.isNeutralInput()

        // then
        assertThat(result).isFalse()
    }
    //endregion

    //region isStance() tests
    @Test
    fun `isStance returns DGF for DGF dot 1 input`() {
        // given
        val move = createMove("DGF.1")

        // when
        val result = move.isStance()

        // then
        assertThat(result).isEqualTo("DGF")
    }

    @Test
    fun `isStance returns FLE for FLE dot 1 input`() {
        // given
        val move = createMove("FLE.1")

        // when
        val result = move.isStance()

        // then
        assertThat(result).isEqualTo("FLE")
    }

    @Test
    fun `isStance returns KIN for KIN dot 1 input`() {
        // given
        val move = createMove("KIN.1")

        // when
        val result = move.isStance()

        // then
        assertThat(result).isEqualTo("KIN")
    }

    @Test
    fun `isStance returns IND for IND dot 1 input`() {
        // given
        val move = createMove("IND.1")

        // when
        val result = move.isStance()

        // then
        assertThat(result).isEqualTo("IND")
    }

    @Test
    fun `isStance returns BT for BT dot 1 input`() {
        // given
        val move = createMove("BT.1")

        // when
        val result = move.isStance()

        // then
        assertThat(result).isEqualTo("BT")
    }

    @Test
    fun `isStance returns BDS for BDS dot 1 input`() {
        // given
        val move = createMove("BDS.1")

        // when
        val result = move.isStance()

        // then
        assertThat(result).isEqualTo("BDS")
    }

    @Test
    fun `isStance returns null for neutral input 1`() {
        // given
        val move = createMove("1")

        // when
        val result = move.isStance()

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `isStance returns null for df plus 1 input`() {
        // given
        val move = createMove("df+1")

        // when
        val result = move.isStance()

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `isStance returns null for f comma n comma d comma df plus 1 input`() {
        // given
        val move = createMove("f,n,d,df+1")

        // when
        val result = move.isStance()

        // then
        assertThat(result).isNull()
    }
    //endregion

    //region getCategoryName() tests
    @Test
    fun `getCategoryName returns Heat for heat move`() {
        // given
        val move = createMove("H.2+3", isHeat = true)

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("Heat")
    }

    @Test
    fun `getCategoryName returns DGF for DGF dot 1 input`() {
        // given
        val move = createMove("DGF.1")

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("DGF")
    }

    @Test
    fun `getCategoryName returns FLE for FLE dot 3 plus 4 input`() {
        // given
        val move = createMove("FLE.3+4")

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("FLE")
    }

    @Test
    fun `getCategoryName returns df for df plus 1 input`() {
        // given
        val move = createMove("df+1")

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("df")
    }

    @Test
    fun `getCategoryName returns f for f plus 2 input`() {
        // given
        val move = createMove("f+2")

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("f")
    }

    @Test
    fun `getCategoryName returns Motion Input for qcf plus 1 input`() {
        // given
        val move = createMove("qcf+1")

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("Motion Input")
    }

    @Test
    fun `getCategoryName returns Crouch for FC dot d plus 1 input`() {
        // given
        val move = createMove("FC.d+1")

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("Crouch")
    }

    @Test
    fun `getCategoryName returns WS for ws1 input`() {
        // given
        val move = createMove("ws1")

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("WS")
    }

    @Test
    fun `getCategoryName returns BT Back Turned for BT dot 1 input`() {
        // given
        val move = createMove("BT.1")

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("BT")
    }

    @Test
    fun `getCategoryName returns Throws for move with throw in notes`() {
        // given
        val move = createMove("1+3", notes = listOf("Homing", "Throw break 1 or 2"))

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("Throws")
    }

    @Test
    fun `getCategoryName returns n for neutral input 1`() {
        // given
        val move = createMove("1")

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("n")
    }

    @Test
    fun `getCategoryName returns n for neutral input 4`() {
        // given
        val move = createMove("4")

        // when
        val result = move.getCategoryName()

        // then
        assertThat(result).isEqualTo("n")
    }

    @Test
    fun `getCategorySortOrder returns 1 for Heat`() {
        // given
        val category = "Heat"

        // when
        val result = category.getCategorySortOrder()

        // then
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun `getCategorySortOrder returns 2 for n`() {
        // given
        val category = "n"

        // when
        val result = category.getCategorySortOrder()

        // then
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun `getCategorySortOrder returns 3 for f`() {
        // given
        val category = "f"

        // when
        val result = category.getCategorySortOrder()

        // then
        assertThat(result).isEqualTo(3)
    }

    @Test
    fun `getCategorySortOrder returns 4 for df`() {
        // given
        val category = "df"

        // when
        val result = category.getCategorySortOrder()

        // then
        assertThat(result).isEqualTo(4)
    }

    @Test
    fun `getCategorySortOrder returns 5 for d`() {
        // given
        val category = "d"

        // when
        val result = category.getCategorySortOrder()

        // then
        assertThat(result).isEqualTo(5)
    }

    @Test
    fun `getCategorySortOrder returns 6 for db`() {
        // given
        val category = "db"

        // when
        val result = category.getCategorySortOrder()

        // then
        assertThat(result).isEqualTo(6)
    }

    @Test
    fun `getCategorySortOrder returns 7 for b`() {
        // given
        val category = "b"

        // when
        val result = category.getCategorySortOrder()

        // then
        assertThat(result).isEqualTo(7)
    }

    @Test
    fun `getCategorySortOrder returns 9 for Motion Input`() {
        // given
        val category = "Motion Input"

        // when
        val result = category.getCategorySortOrder()

        // then
        assertThat(result).isEqualTo(9)
    }

    @Test
    fun `getCategorySortOrder returns 10 for Crouch`() {
        // given
        val category = "Crouch"

        // when
        val result = category.getCategorySortOrder()

        // then
        assertThat(result).isEqualTo(10)
    }

    @Test
    fun `getCategorySortOrder returns 11 for WS`() {
        // given
        val category = "WS"

        // when
        val result = category.getCategorySortOrder()

        // then
        assertThat(result).isEqualTo(11)
    }

    @Test
    fun `getCategorySortOrder returns 14 for unknown category`() {
        // given
        val category = "DGF"

        // when
        val result = category.getCategorySortOrder()

        // then
        assertThat(result).isEqualTo(14)
    }

    @Test
    fun `getCategorySortOrder returns 14 for FLE`() {
        // given
        val category = "FLE"

        // when
        val result = category.getCategorySortOrder()

        // then
        assertThat(result).isEqualTo(14)
    }
    //endregion


    private fun createMove(
        input: String,
        notes: List<String> = emptyList(),
        isHeat: Boolean = false
    ) = Move(
        charName = "Charname",
        id = "test-${input}",
        name = "Test Move",
        input = input,
        level = "m",
        damage = "10",
        startup = "i10",
        recoveryOnWhiff = "r20",
        crushes = listOf(),
        onBlock = "+0",
        onHit = "+5",
        onCH = null,
        notes = notes,
        aliases = listOf(),
        image = null,
        videoId = null,
        alt = null,
        isHeat = isHeat
    )
}