package com.example.wikiwavu.usecase

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.example.core.domain.DataError
import com.example.core.domain.Result
import com.example.wikiwavu.WavuError
import com.example.wikiwavu.data.MoveDto
import com.example.wikiwavu.data.MoveListResponseDto
import com.example.wikiwavu.data.WavuWikiDataSource
import com.example.wikiwavu.domain.model.Character
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class DownloadMoveListUseCaseTest {
    private lateinit var mockDataSource: MockWavuWikiDataSource
    private lateinit var useCase: DownloadMoveListUseCase

    @BeforeTest
    fun setup() {
        mockDataSource = MockWavuWikiDataSource()
        useCase = DownloadMoveListUseCase(mockDataSource)
    }

    // region Success Cases - Simple Moves
    @Test
    fun `invoke returns success with simple move without parent`() = runTest {
        // Given
        val character = createTestCharacter("Yoshimitsu")
        val moveDto = MoveDto(
            id = "Yoshimitsu-1",
            name = "Jab",
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
            notes = "Recovers 2f faster on hit or block (t27 r17)",
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character)

        // Then
        assertThat(result).isNotNull()
        result as Result.Success

        val moveList = result.data
        assertThat(moveList.character).isEqualTo(character)
        assertThat(moveList.moveList).hasSize(1)

        val move = moveList.moveList.first()
        assertThat(move.charName).isEqualTo("Yoshimitsu")
        assertThat(move.id).isEqualTo("1")
        assertThat(move.input).isEqualTo("1")
        assertThat(move.name).isEqualTo("Jab")
        assertThat(move.level).isEqualTo("h")
        assertThat(move.damage).isEqualTo("5")
        assertThat(move.startup).isEqualTo("i10")
        assertThat(move.recoveryOnWhiff).isEqualTo("r19")
        assertThat(move.totalFrames).isEqualTo("29")
        assertThat(move.onBlock).isEqualTo("+1")
        assertThat(move.onHit).isEqualTo("+8")
        assertThat(move.notes).hasSize(1)
        assertThat(move.notes.first()).isEqualTo("Recovers 2f faster on hit or block (t27 r17)")
        assertThat(move.isHeat).isFalse()
        assertThat(move.isPowerCrush).isFalse()
        assertThat(move.isHoming).isFalse()
    }
    // endregion

    // region Success Cases - Parent-Child Relationships
    @Test
    fun `invoke correctly aggregates data from parent move`() = runTest {
        // Given
        val character = createTestCharacter("Yoshimitsu")
        val parentMove = MoveDto(
            id = "Yoshimitsu-1",
            name = "Jab",
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
        val childMove = MoveDto(
            id = "Yoshimitsu-1,1",
            name = "Naguri Kabuto Wari",
            input = ",1",
            parent = "Yoshimitsu-1",
            target = ",m",
            damage = ",19",
            startup = ",i23",
            recv = "r34 1SS",
            tot = "67",
            crush = null,
            block = "-9",
            hit = "+4c",
            ch = "+6a",
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(parentMove, childMove)))

        // When
        val result = useCase.invoke(character)

        // Then
        assertThat(result).isNotNull()
        result as Result.Success

        val moveList = result.data.moveList
        assertThat(moveList).hasSize(2)

        // Check child move has aggregated data from parent
        val child = moveList.find { it.id == "11" }
        assertThat(child).isNotNull()
        assertThat(child!!.level).isEqualTo("h,m")
        assertThat(child.damage).isEqualTo("5,19")
        assertThat(child.startup).isEqualTo("i10") // Should get root startup
        assertThat(child.parent).isEqualTo("Yoshimitsu-1")
    }

    @Test
    fun `invoke correctly handles deep parent chain for data aggregation`() = runTest {
        // Given - Three level deep chain: grandparent -> parent -> child
        val character = createTestCharacter("Kazuya")
        val grandparent = MoveDto(
            id = "Kazuya-1",
            input = "1",
            parent = null,
            target = "h",
            damage = "5",
            startup = "i10",
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
        val parent = MoveDto(
            id = "Kazuya-1,1",
            input = ",1",
            parent = "Kazuya-1",
            target = ",h",
            damage = ",4",
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
            target = ",m",
            damage = ",6",
            startup = null,
            recv = "r20",
            tot = "40",
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
        mockDataSource.mockResponse = Result.Success(
            createMoveListResponse(listOf(grandparent, parent, child))
        )

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        val moves = result.data.moveList

        // Check the deepest child aggregated all data correctly
        val deepChild = moves.find { it.id == "112" }
        assertThat(deepChild).isNotNull()
        assertThat(deepChild!!.level).isEqualTo("h,h,m")
        assertThat(deepChild.damage).isEqualTo("5,4,6")
        assertThat(deepChild.startup).isEqualTo("i10") // From grandparent
    }

    @Test
    fun `invoke correctly finds root startup from parent chain`() = runTest {
        // Given
        val character = createTestCharacter("TestChar")
        val root = MoveDto(
            id = "TestChar-1",
            input = "1",
            parent = null,
            target = "h",
            damage = "10",
            startup = "i12",
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
            id = "TestChar-1,2",
            input = ",2",
            parent = "TestChar-1",
            target = ",m",
            damage = ",15",
            startup = null, // No startup defined on child
            recv = "r25",
            tot = "50",
            crush = null,
            block = "-5",
            hit = "+3",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(root, child)))

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        val childMove = result.data.moveList.find { it.id == "12" }
        assertThat(childMove).isNotNull()
        assertThat(childMove!!.startup).isEqualTo("i12") // Should inherit from root
    }
    // endregion

    // region Success Cases - Boolean Flags
    @Test
    fun `invoke correctly detects Heat Engager from notes`() = runTest {
        // Given
        val character = createTestCharacter("TestChar")
        val moveDto = MoveDto(
            id = "TestChar-df2",
            input = "d/f+2",
            parent = null,
            target = "m",
            damage = "15",
            startup = "i15",
            recv = null,
            tot = null,
            crush = null,
            block = null,
            hit = null,
            ch = null,
            notes = "This move is a Heat Engager and launches on counter hit",
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        val move = result.data.moveList.first()
        assertThat(move.isHeat).isTrue()
    }

    @Test
    fun `invoke correctly detects Power Crush from crush field`() = runTest {
        // Given
        val character = createTestCharacter("TestChar")
        val moveDto = MoveDto(
            id = "TestChar-f23",
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
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        val move = result.data.moveList.first()
        assertThat(move.isPowerCrush).isTrue()
    }

    @Test
    fun `invoke correctly detects Homing from notes`() = runTest {
        // Given
        val character = createTestCharacter("Yoshimitsu")
        val moveDto = MoveDto(
            id = "Yoshimitsu-1+3",
            name = "Oni Killer",
            input = "1+3",
            parent = null,
            target = "t",
            damage = "35",
            startup = "i12~14",
            recv = "r25",
            tot = "39",
            crush = null,
            block = "-3",
            hit = "+1d",
            ch = null,
            notes = """
                <div class="plainlist">
                * 
                <div
                  style="display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;"
                  class="movedata-icon border-blue homing"
                >Homing</div>
                * Throw break 1 or 2</div>
            """.trimIndent(),
            alias = null,
            image = null,
            video = "File:t8-p2-yoshimitsu-1+3.mp4",
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        val move = result.data.moveList.first()
        assertThat(move.isHoming).isTrue()
    }

    @Test
    fun `invoke sets all boolean flags to false when not present`() = runTest {
        // Given
        val character = createTestCharacter("TestChar")
        val moveDto = MoveDto(
            id = "TestChar-1",
            input = "1",
            parent = null,
            target = "h",
            damage = "10",
            startup = "i10",
            recv = null,
            tot = null,
            crush = null,
            block = null,
            hit = null,
            ch = null,
            notes = "Regular jab with no special properties",
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        val move = result.data.moveList.first()
        assertThat(move.isHeat).isFalse()
        assertThat(move.isPowerCrush).isFalse()
        assertThat(move.isHoming).isFalse()
    }
    // endregion

    // region Success Cases - String Parsing
    @Test
    fun `invoke correctly cleans HTML entities from notes`() = runTest {
        // Given
        val character = createTestCharacter("TestChar")
        val moveDto = MoveDto(
            id = "TestChar-1",
            input = "1",
            parent = null,
            target = "h",
            damage = "10",
            startup = "i10",
            recv = null,
            tot = null,
            crush = null,
            block = null,
            hit = null,
            ch = null,
            notes = "&lt;div&gt;Test &amp; note with &quot;quotes&quot;&lt;/div&gt;",
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        val move = result.data.moveList.first()
        assertThat(move.notes.first()).isEqualTo("Test & note with \"quotes\"")
    }

    @Test
    fun `invoke correctly parses multiple notes from HTML list`() = runTest {
        // Given
        val character = createTestCharacter("Yoshimitsu")
        val moveDto = MoveDto(
            id = "Yoshimitsu-11",
            input = "1,1",
            parent = null,
            target = "h,m",
            damage = "5,19",
            startup = "i10",
            recv = null,
            tot = null,
            crush = null,
            block = null,
            hit = null,
            ch = null,
            notes = """
                <div class="plainlist">
                * Floor Break
                * Weapon
                * Combo from 1st hit with 1f delay
                * Input can be delayed 3f
                </div>
            """.trimIndent(),
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        val move = result.data.moveList.first()
        assertThat(move.notes).hasSize(4)
        assertThat(move.notes).contains("Floor Break")
        assertThat(move.notes).contains("Weapon")
        assertThat(move.notes).contains("Combo from 1st hit with 1f delay")
        assertThat(move.notes).contains("Input can be delayed 3f")
    }

    @Test
    fun `invoke correctly parses aliases from HTML list`() = runTest {
        // Given
        val character = createTestCharacter("Yoshimitsu")
        val moveDto = MoveDto(
            id = "Yoshimitsu-1+2+3",
            input = "1+2+3",
            parent = null,
            target = "m",
            damage = "0",
            startup = "i22~40",
            recv = null,
            tot = null,
            crush = null,
            block = null,
            hit = null,
            ch = null,
            notes = null,
            alias = "1SS.1+2+3",
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        val move = result.data.moveList.first()
        assertThat(move.aliases).hasSize(1)
        assertThat(move.aliases.first()).isEqualTo("1ss1+2+3")
    }

    @Test
    fun `invoke correctly parses multiple aliases from HTML dotlist`() = runTest {
        // Given
        val character = createTestCharacter("Yoshimitsu")
        val moveDto = MoveDto(
            id = "Yoshimitsu-11",
            input = "1,1",
            parent = null,
            target = "h,m",
            damage = "5,19",
            startup = "i10",
            recv = null,
            tot = null,
            crush = null,
            block = null,
            hit = null,
            ch = null,
            notes = null,
            alias = """
                <div class="dotlist">
                
                * 1SS.1,1
                * 1,1SS.1
                </div>
            """.trimIndent(),
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        val move = result.data.moveList.first()
        assertThat(move.aliases).hasSize(2)
        assertThat(move.aliases).contains("1ss11")
        assertThat(move.aliases).contains("11ss1")
    }

    @Test
    fun `invoke correctly cleans move input with cleanMoveInput extension`() = runTest {
        // Given
        val character = createTestCharacter("TestChar")
        val moveDto = MoveDto(
            id = "TestChar-f, f+3",
            input = "f, f+3",
            parent = null,
            target = "m",
            damage = "25",
            startup = "i20",
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
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        val move = result.data.moveList.first()
        // "f, f+3" after substringAfter("-") becomes "f, f+3"
        // cleanMoveInput removes spaces and commas: "ff+3" -> "ff3"
        assertThat(move.id).isEqualTo("ff3")
    }

    @Test
    fun `invoke handles empty notes gracefully`() = runTest {
        // Given
        val character = createTestCharacter("TestChar")
        val moveDto = MoveDto(
            id = "TestChar-1",
            input = "1",
            parent = null,
            target = "h",
            damage = "10",
            startup = "i10",
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
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        val move = result.data.moveList.first()
        assertThat(move.notes).isEmpty()
    }

    @Test
    fun `invoke handles empty aliases gracefully`() = runTest {
        // Given
        val character = createTestCharacter("TestChar")
        val moveDto = MoveDto(
            id = "TestChar-1",
            input = "1",
            parent = null,
            target = "h",
            damage = "10",
            startup = "i10",
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
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        val move = result.data.moveList.first()
        assertThat(move.aliases).isEmpty()
    }

    @Test
    fun `invoke correctly parses multiple crush values from HTML`() = runTest {
        // Given
        val character = createTestCharacter("Yoshimitsu")
        val moveDto = MoveDto(
            id = "Yoshimitsu-KIN.1+2",
            input = "KIN.1+2",
            parent = null,
            target = "m,m,m,m",
            damage = "4,4,4,24",
            startup = "i12~13",
            recv = null,
            tot = null,
            crush = "<div class=\"plainlist\">\n* is1~20\n* js25~39\n* fs40~42</div>",
            block = null,
            hit = null,
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        val move = result.data.moveList.first()
        assertThat(move.crushes).hasSize(3)
        assertThat(move.crushes).isEqualTo(listOf("is1~20", "js25~39", "fs40~42"))
    }

    @Test
    fun `invoke correctly parses single crush value`() = runTest {
        // Given
        val character = createTestCharacter("TestChar")
        val moveDto = MoveDto(
            id = "TestChar-f23",
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
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        val move = result.data.moveList.first()
        assertThat(move.crushes).hasSize(1)
        assertThat(move.crushes.first()).isEqualTo("pc8~")
    }

    @Test
    fun `invoke handles empty crush gracefully`() = runTest {
        // Given
        val character = createTestCharacter("TestChar")
        val moveDto = MoveDto(
            id = "TestChar-1",
            input = "1",
            parent = null,
            target = "h",
            damage = "10",
            startup = "i10",
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
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        val move = result.data.moveList.first()
        assertThat(move.crushes).isEmpty()
    }

    @Test
    fun `invoke correctly cleans HTML from crush field`() = runTest {
        // Given
        val character = createTestCharacter("TestChar")
        val moveDto = MoveDto(
            id = "TestChar-df2",
            input = "d/f+2",
            parent = null,
            target = "m",
            damage = "15",
            startup = "i15",
            recv = null,
            tot = null,
            crush = "<div class=\"plainlist\">\n* ps3~9</div>",
            block = null,
            hit = null,
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        val move = result.data.moveList.first()
        assertThat(move.crushes).hasSize(1)
        assertThat(move.crushes.first()).isEqualTo("ps3~9")
    }
    // endregion

    // region Success Cases - Field Mapping
    @Test
    fun `invoke correctly maps all DTO fields to domain model`() = runTest {
        // Given
        val character = createTestCharacter("Yoshimitsu")
        val moveDto = MoveDto(
            id = "Yoshimitsu-1+2+3+4",
            name = "Ki Charge",
            input = "1+2+3+4",
            parent = null,
            target = null,
            damage = null,
            startup = null,
            recv = "r55",
            tot = null,
            crush = null,
            block = null,
            hit = null,
            ch = null,
            notes = "Can't block for 5 seconds",
            alias = null,
            image = null,
            video = "File:t8-p2-yoshimitsu-1+2+3+4.mp4",
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        val move = result.data.moveList.first()
        assertThat(move.charName).isEqualTo("Yoshimitsu")
        assertThat(move.id).isEqualTo("1+2+3+4")
        assertThat(move.input).isEqualTo("1+2+3+4")
        assertThat(move.name).isEqualTo("Ki Charge")
        assertThat(move.level).isNull()
        assertThat(move.parent).isNull()
        assertThat(move.damage).isNull()
        assertThat(move.startup).isNull()
        assertThat(move.recoveryOnWhiff).isEqualTo("r55")
        assertThat(move.totalFrames).isNull()
        assertThat(move.crushes).isEmpty()
        assertThat(move.onBlock).isNull()
        assertThat(move.onHit).isNull()
        assertThat(move.onCH).isNull()
        assertThat(move.image).isNull()
        assertThat(move.videoId).isEqualTo("File:t8-p2-yoshimitsu-1+2+3+4.mp4")
        assertThat(move.alt).isNull()
    }

    @Test
    fun `invoke correctly handles multi-hit moves with complex data`() = runTest {
        // Given
        val character = createTestCharacter("Yoshimitsu")
        val moveDto = MoveDto(
            id = "Yoshimitsu-KIN.1+2",
            name = "Ashura Blade",
            input = "KIN.1+2",
            parent = null,
            target = "m,m,m,m",
            damage = "4,4,4,24",
            startup = "i12~13 i6~7 i7~8 i11~13",
            recv = "r37 BT",
            tot = "80",
            crush = "<div class=\"plainlist\">\n* is1~20\n* js25~39\n* fs40~42</div>",
            block = "-6",
            hit = "+11a",
            ch = null,
            notes = "Combos from any hit",
            alias = null,
            image = null,
            video = "File:t8-p2-yoshimitsu-kin.1+2.mp4",
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(moveDto)))

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        val move = result.data.moveList.first()
        assertThat(move.level).isEqualTo("m,m,m,m")
        assertThat(move.damage).isEqualTo("4,4,4,24")
        assertThat(move.startup).isEqualTo("i12~13 i6~7 i7~8 i11~13")
        assertThat(move.crushes).hasSize(3)
        assertThat(move.crushes).isEqualTo(listOf("is1~20", "js25~39", "fs40~42"))
    }
    // endregion

    // region Error Cases
    @Test
    fun `invoke returns error when data source fails`() = runTest {
        // Given
        val character = createTestCharacter("Yoshimitsu")
        mockDataSource.mockResponse = Result.Error(DataError.Remote.SERVER_ERROR)

        // When
        val result = useCase.invoke(character)

        // Then
        assertThat(result).isNotNull()
        result as Result.Error
        assertThat(result.error).isEqualTo(WavuError.DOWNLOAD_ERROR)
    }

    @Test
    fun `invoke returns error when data source returns network error`() = runTest {
        // Given
        val character = createTestCharacter("TestChar")
        mockDataSource.mockResponse = Result.Error(DataError.Remote.NO_INTERNET)

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Error
        assertThat(result.error).isEqualTo(WavuError.DOWNLOAD_ERROR)
    }

    @Test
    fun `invoke returns error when data source returns timeout`() = runTest {
        // Given
        val character = createTestCharacter("TestChar")
        mockDataSource.mockResponse = Result.Error(DataError.Remote.REQUEST_TIMEOUT)

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Error
        assertThat(result.error).isEqualTo(WavuError.DOWNLOAD_ERROR)
    }

    @Test
    fun `invoke returns error when data source returns serialization error`() = runTest {
        // Given
        val character = createTestCharacter("TestChar")
        mockDataSource.mockResponse = Result.Error(DataError.Remote.SERIALIZATION_ERROR)

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Error
        assertThat(result.error).isEqualTo(WavuError.DOWNLOAD_ERROR)
    }
    // endregion

    // region Edge Cases
    @Test
    fun `invoke handles empty move list response`() = runTest {
        // Given
        val character = createTestCharacter("TestChar")
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(emptyList()))

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        assertThat(result.data.moveList).isEmpty()
    }

    @Test
    fun `invoke handles move with only required fields`() = runTest {
        // Given
        val character = createTestCharacter("TestChar")
        val minimalMove = MoveDto(
            id = "TestChar-1",
            input = "1",
            parent = null,
            target = null,
            name = null,
            damage = null,
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
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(minimalMove)))

        // When
        val result = useCase.invoke(character)

        // Then
        result as Result.Success
        val move = result.data.moveList.first()
        assertThat(move.id).isEqualTo("1")
        assertThat(move.input).isEqualTo("1")
        assertThat(move.level).isNull()
        assertThat(move.name).isNull()
        assertThat(move.damage).isNull()
        assertThat(move.notes).isEmpty()
        assertThat(move.aliases).isEmpty()
    }

    @Test
    fun `invoke handles parent reference to non-existent move gracefully`() = runTest {
        // Given - Child references a parent that doesn't exist in the list
        val character = createTestCharacter("TestChar")
        val orphanedChild = MoveDto(
            id = "TestChar-1,2",
            input = ",2",
            parent = "TestChar-1", // This parent doesn't exist
            target = ",m",
            damage = ",15",
            startup = null,
            recv = "r25",
            tot = "50",
            crush = null,
            block = "-5",
            hit = "+3",
            ch = null,
            notes = null,
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        mockDataSource.mockResponse = Result.Success(createMoveListResponse(listOf(orphanedChild)))

        // When
        val result = useCase.invoke(character)

        // Then - Should not crash and should handle gracefully
        result as Result.Success
        val move = result.data.moveList.first()
        assertThat(move.parent).isEqualTo("TestChar-1")
        // Data should only contain the child's own data
        assertThat(move.level).isEqualTo(",m")
        assertThat(move.damage).isEqualTo(",15")
        assertThat(move.startup).isNull()
    }
    // endregion

    // region Helper Methods
    private fun createTestCharacter(name: String) = Character(
        name = name,
        portraitUrl = "https://example.com/$name.png",
        wavuPageUrl = "https://wavu.wiki/$name",
        alias = listOf(name.lowercase())
    )

    private fun createMoveListResponse(moves: List<MoveDto>) = MoveListResponseDto(
        cargoQuery = moves.map { MoveListResponseDto.Title(it) }
    )
    // endregion

    // region Mock Data Source
    private class MockWavuWikiDataSource : WavuWikiDataSource {
        var mockResponse: Result<MoveListResponseDto, DataError.Remote>? = null

        override suspend fun fetchMoveList(char: String): Result<MoveListResponseDto, DataError.Remote> {
            return mockResponse ?: Result.Error(DataError.Remote.UNKNOWN)
        }
    }
    // endregion
}