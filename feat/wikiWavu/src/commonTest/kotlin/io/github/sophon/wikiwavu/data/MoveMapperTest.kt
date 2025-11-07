package io.github.sophon.wikiwavu.data

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import kotlin.test.Test

class MoveMapperTest {
    // region Properties - Never Null for Tekken 8
    @Test
    fun `mapToDomain ensures all boolean properties are never null`() {
        // Given
        val moveDto = MoveDto(
            id = "Jin-1",
            input = "1",
            parent = null,
            target = "h",
            damage = "5",
            startup = "i10",
            recv = "r19",
            tot = "29",
            crush = null,
            block = "+1",
            hit = "+8",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Jin", movesById)

        // Then
        assertThat(result.t8Properties).isNotNull()
        assertThat(result.t8Properties?.isHeat).isNotNull()
        assertThat(result.t8Properties?.isPowerCrush).isNotNull()
        assertThat(result.t8Properties?.isHoming).isNotNull()
        assertThat(result.t8Properties?.isHeat).isEqualTo(false)
        assertThat(result.t8Properties?.isPowerCrush).isEqualTo(false)
        assertThat(result.t8Properties?.isHoming).isEqualTo(false)
    }

    @Test
    fun `mapToDomain sets heat to true when move is heat engager`() {
        // Given
        val moveDto = MoveDto(
            id = "Jin-df2",
            input = "d/f+2",
            parent = null,
            target = "m",
            damage = "15",
            startup = "i15",
            recv = null,
            tot = null,
            crush = null,
            block = "-12",
            hit = "Launch",
            ch = null,
            notes = "Heat Engager on block or hit",
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Jin", movesById)

        // Then
        assertThat(result.t8Properties?.isHeat).isEqualTo(true)
        assertThat(result.t8Properties?.isPowerCrush).isEqualTo(false)
        assertThat(result.t8Properties?.isHoming).isEqualTo(false)
    }

    @Test
    fun `mapToDomain sets power crush to true when crush contains pc`() {
        // Given
        val moveDto = MoveDto(
            id = "Paul-f23",
            input = "f+2,3",
            parent = null,
            target = "m,m",
            damage = "12,20",
            startup = "i18",
            recv = null,
            tot = null,
            crush = "pc8~",
            block = "-12",
            hit = "+5a",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Paul", movesById)

        // Then
        assertThat(result.t8Properties?.isPowerCrush).isEqualTo(true)
        assertThat(result.t8Properties?.isHeat).isEqualTo(false)
        assertThat(result.t8Properties?.isHoming).isEqualTo(false)
    }

    @Test
    fun `mapToDomain sets homing to true when notes contain homing`() {
        // Given
        val moveDto = MoveDto(
            id = "Jin-f4",
            input = "f+4",
            parent = null,
            target = "m",
            damage = "20",
            startup = "i15",
            recv = null,
            tot = null,
            crush = null,
            block = "-9",
            hit = "+2",
            ch = null,
            notes = "Homing move that tracks sidewalk",
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Jin", movesById)

        // Then
        assertThat(result.t8Properties?.isHoming).isEqualTo(true)
        assertThat(result.t8Properties?.isHeat).isEqualTo(false)
        assertThat(result.t8Properties?.isPowerCrush).isEqualTo(false)
    }

    @Test
    fun `mapToDomain handles move with multiple properties set to true`() {
        // Given
        val moveDto = MoveDto(
            id = "Jin-f1+2",
            input = "f+1+2",
            parent = null,
            target = "m",
            damage = "25",
            startup = "i20",
            recv = null,
            tot = null,
            crush = "pc12~",
            block = "-15",
            hit = "KND",
            ch = null,
            notes = "Heat Engager and Homing attack",
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Jin", movesById)

        // Then
        assertThat(result.t8Properties?.isHeat).isEqualTo(true)
        assertThat(result.t8Properties?.isPowerCrush).isEqualTo(true)
        assertThat(result.t8Properties?.isHoming).isEqualTo(true)
    }
    // endregion

    // region Stance Detection - Valid Stances
    @Test
    fun `mapToDomain detects stance from zen dot 1 input`() {
        // Given
        val moveDto = MoveDto(
            id = "Jin-zen.1",
            input = "zen.1",
            parent = null,
            target = "h",
            damage = "12",
            startup = "i12",
            recv = null,
            tot = null,
            crush = null,
            block = "+2",
            hit = "+8",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Jin", movesById)

        // Then
        assertThat(result.t8Properties?.stance).isEqualTo("zen")
    }

    @Test
    fun `mapToDomain detects stance from des dot f21 input`() {
        // Given
        val moveDto = MoveDto(
            id = "Yoshimitsu-des.f21",
            input = "des.f+2,1",
            parent = null,
            target = "m,h",
            damage = "15,10",
            startup = "i18",
            recv = null,
            tot = null,
            crush = null,
            block = "-5",
            hit = "+6",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Yoshimitsu", movesById)

        // Then
        assertThat(result.t8Properties?.stance).isEqualTo("des")
    }

    @Test
    fun `mapToDomain detects stance from kin dot 1+2 input`() {
        // Given
        val moveDto = MoveDto(
            id = "Yoshimitsu-kin.1+2",
            input = "kin.1+2",
            parent = null,
            target = "m",
            damage = "20",
            startup = "i15",
            recv = null,
            tot = null,
            crush = null,
            block = "-10",
            hit = "KND",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Yoshimitsu", movesById)

        // Then
        assertThat(result.t8Properties?.stance).isEqualTo("kin")
    }

    @Test
    fun `mapToDomain detects stance from fle dot 3+4 input`() {
        // Given
        val moveDto = MoveDto(
            id = "Yoshimitsu-fle.3+4",
            input = "fle.3+4",
            parent = null,
            target = "L",
            damage = "22",
            startup = "i20",
            recv = null,
            tot = null,
            crush = null,
            block = "-15",
            hit = "KND",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Yoshimitsu", movesById)

        // Then
        assertThat(result.t8Properties?.stance).isEqualTo("fle")
    }

    @Test
    fun `mapToDomain detects uppercase stance and normalizes to lowercase`() {
        // Given
        val moveDto = MoveDto(
            id = "Jin-ZEN.2",
            input = "ZEN.2",
            parent = null,
            target = "m",
            damage = "18",
            startup = "i14",
            recv = null,
            tot = null,
            crush = null,
            block = "-8",
            hit = "+3",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Jin", movesById)

        // Then
        assertThat(result.t8Properties?.stance).isEqualTo("zen")
    }
    // endregion

    // region Stance Detection - Not Stances
    @Test
    fun `mapToDomain returns empty stance for motion input wr`() {
        // Given
        val moveDto = MoveDto(
            id = "Jin-wr2",
            input = "wr2",
            parent = null,
            target = "m",
            damage = "22",
            startup = "i15",
            recv = null,
            tot = null,
            crush = null,
            block = "-10",
            hit = "KND",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Jin", movesById)

        // Then
        assertThat(result.t8Properties?.stance).isEqualTo("")
    }

    @Test
    fun `mapToDomain returns empty stance for motion input ff`() {
        // Given
        val moveDto = MoveDto(
            id = "Jin-ff3",
            input = "ff3",
            parent = null,
            target = "m",
            damage = "25",
            startup = "i20",
            recv = null,
            tot = null,
            crush = null,
            block = "-12",
            hit = "Launch",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Jin", movesById)

        // Then
        assertThat(result.t8Properties?.stance).isEqualTo("")
    }

    @Test
    fun `mapToDomain returns empty stance for motion input qcf`() {
        // Given
        val moveDto = MoveDto(
            id = "Akuma-qcf1",
            input = "qcf1",
            parent = null,
            target = "h",
            damage = "20",
            startup = "i14",
            recv = null,
            tot = null,
            crush = null,
            block = "-8",
            hit = "+3",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Akuma", movesById)

        // Then
        assertThat(result.t8Properties?.stance).isEqualTo("")
    }

    @Test
    fun `mapToDomain returns empty stance for motion input qcb`() {
        // Given
        val moveDto = MoveDto(
            id = "Akuma-qcb2",
            input = "qcb2",
            parent = null,
            target = "m",
            damage = "18",
            startup = "i16",
            recv = null,
            tot = null,
            crush = null,
            block = "-9",
            hit = "+2",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Akuma", movesById)

        // Then
        assertThat(result.t8Properties?.stance).isEqualTo("")
    }

    @Test
    fun `mapToDomain returns empty stance for motion input fc`() {
        // Given
        val moveDto = MoveDto(
            id = "Jin-fc4",
            input = "fc4",
            parent = null,
            target = "L",
            damage = "12",
            startup = "i11",
            recv = null,
            tot = null,
            crush = null,
            block = "-13",
            hit = "+2",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Jin", movesById)

        // Then
        assertThat(result.t8Properties?.stance).isEqualTo("")
    }

    @Test
    fun `mapToDomain returns empty stance for input without digits`() {
        // Given
        val moveDto = MoveDto(
            id = "Jin-zen",
            input = "zen",
            parent = null,
            target = null,
            damage = null,
            startup = null,
            recv = null,
            tot = null,
            crush = null,
            block = null,
            hit = null,
            ch = null,
            notes = "Transition to zen stance",
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Jin", movesById)

        // Then
        assertThat(result.t8Properties?.stance).isEqualTo("")
    }

    @Test
    fun `mapToDomain returns empty stance for input with less than three letters`() {
        // Given
        val moveDto = MoveDto(
            id = "Jin-df2",
            input = "d/f+2",
            parent = null,
            target = "m",
            damage = "15",
            startup = "i15",
            recv = null,
            tot = null,
            crush = null,
            block = "-12",
            hit = "Launch",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Jin", movesById)

        // Then
        assertThat(result.t8Properties?.stance).isEqualTo("")
    }

    @Test
    fun `mapToDomain returns empty stance for regular move starting with digit`() {
        // Given
        val moveDto = MoveDto(
            id = "Jin-1",
            input = "1",
            parent = null,
            target = "h",
            damage = "5",
            startup = "i10",
            recv = null,
            tot = null,
            crush = null,
            block = "+1",
            hit = "+8",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Jin", movesById)

        // Then
        assertThat(result.t8Properties?.stance).isEqualTo("")
    }

    @Test
    fun `mapToDomain returns empty stance for input with special characters only`() {
        // Given
        val moveDto = MoveDto(
            id = "Jin-1+2",
            input = "1+2",
            parent = null,
            target = "m",
            damage = "20",
            startup = "i14",
            recv = null,
            tot = null,
            crush = null,
            block = "-8",
            hit = "+3",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Jin", movesById)

        // Then
        assertThat(result.t8Properties?.stance).isEqualTo("")
    }
    // endregion

    // region Stance Detection - Edge Cases
    @Test
    fun `mapToDomain handles stance with complex notation after stance name`() {
        // Given
        val moveDto = MoveDto(
            id = "Yoshimitsu-des.bt.1",
            input = "des.bt.1",
            parent = null,
            target = "h",
            damage = "10",
            startup = "i12",
            recv = null,
            tot = null,
            crush = null,
            block = "+2",
            hit = "+8",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Yoshimitsu", movesById)

        // Then
        assertThat(result.t8Properties?.stance).isEqualTo("des")
    }

    @Test
    fun `mapToDomain detects stance with numbers immediately after letters`() {
        // Given
        val moveDto = MoveDto(
            id = "TestChar-abc.3+4",
            input = "abc.3+4",
            parent = null,
            target = "m",
            damage = "20",
            startup = "i15",
            recv = null,
            tot = null,
            crush = null,
            block = "-10",
            hit = "+5",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("TestChar", movesById)

        // Then
        assertThat(result.t8Properties?.stance).isEqualTo("abc")
    }
    // endregion

    // region Parent Traversal
    @Test
    fun `mapToDomain constructs complete input from parent chain`() {
        // Given - Kazuya's 1,1,2 string where each hit builds on parent
        val parent1 = MoveDto(
            id = "Kazuya-1",
            input = "1",
            parent = null,
            target = "h",
            damage = "5",
            startup = "i10",
            recv = null,
            tot = null,
            crush = null,
            block = "+1",
            hit = "+8",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val parent2 = MoveDto(
            id = "Kazuya-1,1",
            input = ",1",
            parent = "Kazuya-1",
            target = "h,h",
            damage = ",5",
            startup = null,
            recv = null,
            tot = null,
            crush = null,
            block = null,
            hit = null,
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val child = MoveDto(
            id = "Kazuya-1,1,2",
            input = ",2",
            parent = "Kazuya-1,1",
            target = "h,h,m",
            damage = ",6",
            startup = null,
            recv = null,
            tot = null,
            crush = null,
            block = "-12",
            hit = "Launch",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(
            parent1.id to parent1,
            parent2.id to parent2,
            child.id to child
        )

        // When
        val result = child.mapToDomain("Kazuya", movesById)

        // Then
        assertThat(result.input).isEqualTo("112")
        assertThat(result.damage).isEqualTo("5,5,6")
    }

    @Test
    fun `mapToDomain gets startup from root parent`() {
        // Given
        val parent = MoveDto(
            id = "Jin-1",
            input = "1",
            parent = null,
            target = "h",
            damage = "5",
            startup = "i10",
            recv = null,
            tot = null,
            crush = null,
            block = "+1",
            hit = "+8",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val child = MoveDto(
            id = "Jin-1,2",
            input = ",2",
            parent = "Jin-1",
            target = "h,h",
            damage = ",8",
            startup = null, // Child doesn't have startup
            recv = null,
            tot = null,
            crush = null,
            block = "-1",
            hit = "+8",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(
            parent.id to parent,
            child.id to child
        )

        // When
        val result = child.mapToDomain("Jin", movesById)

        // Then
        assertThat(result.startup).isEqualTo("i10")
    }
    // endregion

    // region Alias Parsing
    @Test
    fun `mapToDomain parses aliases correctly`() {
        // Given
        val moveDto = MoveDto(
            id = "Jin-ewgf",
            input = "f,n,d/f+2",
            parent = null,
            target = "m",
            damage = "25",
            startup = "i13",
            recv = null,
            tot = null,
            crush = null,
            block = "+5",
            hit = "Launch",
            ch = null,
            notes = null,
            alias = "* ewgf\n* electric",
            image = null,
            video = null,
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Jin", movesById)

        // Then
        assertThat(result.aliases).hasSize(2)
        assertThat(result.aliases[0]).isEqualTo("ewgf")
        assertThat(result.aliases[1]).isEqualTo("electric")
    }
    // endregion

    // region Basic Mapping
    @Test
    fun `mapToDomain maps basic move properties correctly`() {
        // Given
        val moveDto = MoveDto(
            id = "Jin-df2",
            input = "d/f+2",
            parent = null,
            target = "m",
            damage = "15",
            startup = "i15",
            recv = "r25",
            tot = null,
            crush = null,
            block = "-12",
            hit = "Launch",
            ch = null,
            notes = "Launcher",
            alias = null,
            image = null,
            video = "abc123",
            alt = null
        )
        val movesById = mapOf(moveDto.id to moveDto)

        // When
        val result = moveDto.mapToDomain("Jin", movesById)

        // Then
        assertThat(result.charName).isEqualTo("Jin")
        assertThat(result.id).isEqualTo("jin-df2")
        assertThat(result.input).isEqualTo("df2")
        assertThat(result.damage).isEqualTo("15")
        assertThat(result.startup).isEqualTo("i15")
        assertThat(result.recovery).isEqualTo("r25")
        assertThat(result.onBlock).isEqualTo("-12")
        assertThat(result.onHit).isEqualTo("Launch")
        assertThat(result.videoId).isEqualTo("abc123")
        assertThat(result.t8Properties?.level).isEqualTo("m")
    }
    // endregion
}