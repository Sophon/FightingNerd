package io.github.sophon.core.util

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import kotlin.test.Test

class StringExtensionsTest {
    //region Whitespace
    @Test
    fun `removeWhiteSpace removes all spaces`() {
        // Given
        val input = "Dragon Punch Motion"

        // When
        val result = input.removeWhiteSpace()

        // Then
        assertThat(result).isEqualTo("DragonPunchMotion")
    }

    @Test
    fun `removeWhiteSpace removes tabs and newlines`() {
        // Given
        val input = "Dragon\tPunch\nMotion"

        // When
        val result = input.removeWhiteSpace()

        // Then
        assertThat(result).isEqualTo("DragonPunchMotion")
    }

    @Test
    fun `removeWhiteSpace returns same string when no whitespace`() {
        // Given
        val input = "Fireball"

        // When
        val result = input.removeWhiteSpace()

        // Then
        assertThat(result).isEqualTo("Fireball")
    }

    @Test
    fun `removeWhiteSpace returns empty string for whitespace only`() {
        // Given
        val input = "   \t\n  "

        // When
        val result = input.removeWhiteSpace()

        // Then
        assertThat(result).isEqualTo("")
    }

    @Test
    fun `removeWhiteSpace handles empty string`() {
        // Given
        val input = ""

        // When
        val result = input.removeWhiteSpace()

        // Then
        assertThat(result).isEqualTo("")
    }
    //endregion

    //region Drop first and join
    @Test
    fun `dropFirstAndJoin drops first element and rejoins`() {
        // Given
        val input = "one,two,three,four"

        // When
        val result = input.dropFirstAndJoin(',')

        // Then
        assertThat(result).isEqualTo("two,three,four")
    }

    @Test
    fun `dropFirstAndJoin returns empty string when only one element`() {
        // Given
        val input = "only"

        // When
        val result = input.dropFirstAndJoin(',')

        // Then
        assertThat(result).isEqualTo("")
    }

    @Test
    fun `dropFirstAndJoin handles different delimiters`() {
        // Given
        val input = "first|second|third"

        // When
        val result = input.dropFirstAndJoin('|')

        // Then
        assertThat(result).isEqualTo("second|third")
    }

    @Test
    fun `dropFirstAndJoin with no delimiter returns empty string`() {
        // Given
        val input = "nodelimiter"

        // When
        val result = input.dropFirstAndJoin(',')

        // Then
        assertThat(result).isEqualTo("")
    }

    @Test
    fun `dropFirstAndJoin handles empty string`() {
        // Given
        val input = ""

        // When
        val result = input.dropFirstAndJoin(',')

        // Then
        assertThat(result).isEqualTo("")
    }

    @Test
    fun `dropFirstAndJoin handles string with only delimiters`() {
        // Given
        val input = ",,,"

        // When
        val result = input.dropFirstAndJoin(',')

        // Then
        assertThat(result).isEqualTo(",,")
    }
    //endregion

    //region Is at least
    @Test
    fun `isAtLeast returns true when word count is exact`() {
        // Given
        val input = "one two three"

        // When
        val result = input.isAtLeast(3)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `isAtLeast returns true when word count is more`() {
        // Given
        val input = "one two three four five"

        // When
        val result = input.isAtLeast(3)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `isAtLeast returns false when word count is less`() {
        // Given
        val input = "one two"

        // When
        val result = input.isAtLeast(3)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `isAtLeast returns true for empty string with zero count`() {
        // Given
        val input = ""

        // When
        val result = input.isAtLeast(0)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `isAtLeast returns false for empty string with non-zero count`() {
        // Given
        val input = ""

        // When
        val result = input.isAtLeast(1)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `isAtLeast handles single word`() {
        // Given
        val input = "single"

        // When
        val result = input.isAtLeast(1)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `isAtLeast handles multiple spaces between words`() {
        // Given
        val input = "one  two   three"

        // When
        val result = input.isAtLeast(3)

        // Then
        // Note: This will actually be 5 because split(' ') creates empty strings
        assertThat(result).isTrue()
    }

    @Test
    fun `isAtLeast returns true for blank string with zero count`() {
        val input = "   "  // Blank, not empty
        assertThat(input.isAtLeast(0)).isTrue()
    }

    @Test
    fun `isAtLeast returns false for blank string with non-zero count`() {
        val input = "   "  // Blank, not empty
        assertThat(input.isAtLeast(1)).isFalse()
    }

    @Test
    fun `isAtLeast handles multiple spaces between words correctly`() {
        val input = "one  two   three"  // Only 3 actual words
        assertThat(input.isAtLeast(3)).isTrue()
        assertThat(input.isAtLeast(4)).isFalse()
    }
    //endregion

    //region Truncate
    @Test
    fun `truncate returns original string when shorter than max`() {
        // Given
        val input = "Short"

        // When
        val result = input.truncate(10)

        // Then
        assertThat(result).isEqualTo("Short")
    }

    @Test
    fun `truncate returns original string when equal to max`() {
        // Given
        val input = "Exact"

        // When
        val result = input.truncate(5)

        // Then
        assertThat(result).isEqualTo("Exact")
    }

    @Test
    fun `truncate adds ellipsis when longer than max`() {
        // Given
        val input = "This is a very long string that needs truncation"

        // When
        val result = input.truncate(20)

        // Then
        assertThat(result).isEqualTo("This is a very lo...")
        assertThat(result.length).isEqualTo(20)
    }

    @Test
    fun `truncate handles empty string`() {
        // Given
        val input = ""

        // When
        val result = input.truncate(10)

        // Then
        assertThat(result).isEqualTo("")
    }

    @Test
    fun `truncate with very small max length`() {
        // Given
        val input = "Fireball"

        // When
        val result = input.truncate(5)

        // Then
        assertThat(result).isEqualTo("Fi...")
        assertThat(result.length).isEqualTo(5)
    }

    @Test
    fun `truncate with max length of 3 returns only ellipsis`() {
        // Given
        val input = "Fireball"

        // When
        val result = input.truncate(3)

        // Then
        assertThat(result).isEqualTo("...")
        assertThat(result.length).isEqualTo(3)
    }
    //endregion

    //region URL encode
    @Test
    fun `urlEncode encodes spaces`() {
        // Given
        val input = "Dragon Punch"

        // When
        val result = input.urlEncode()

        // Then
        assertThat(result).isEqualTo("Dragon%20Punch")
    }

    @Test
    fun `urlEncode encodes special characters`() {
        // Given
        val input = "Guard Crush (GC)"

        // When
        val result = input.urlEncode()

        // Then
        assertThat(result).isEqualTo("Guard%20Crush%20%28GC%29")
    }

    @Test
    fun `urlEncode returns same string for simple text`() {
        // Given
        val input = "Fireball"

        // When
        val result = input.urlEncode()

        // Then
        assertThat(result).isEqualTo("Fireball")
    }

    @Test
    fun `urlEncode handles empty string`() {
        // Given
        val input = ""

        // When
        val result = input.urlEncode()

        // Then
        assertThat(result).isEqualTo("")
    }

    @Test
    fun `urlEncode encodes ampersand and equals`() {
        // Given
        val input = "test&param=value"

        // When
        val result = input.urlEncode()

        // Then
        assertThat(result).isEqualTo("test%26param%3Dvalue")
    }

    @Test
    fun `urlEncode encodes forward slash`() {
        // Given
        val input = "path/to/resource"

        // When
        val result = input.urlEncode()

        // Then
        assertThat(result).isEqualTo("path%2Fto%2Fresource")
    }
    //endregion

    //region cleanHtml - Basic HTML Cleaning
    @Test
    fun `cleanHtml removes simple HTML tags`() {
        // Given
        val input = "<div>Test</div>"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("Test")
    }

    @Test
    fun `cleanHtml removes multiple HTML tags`() {
        // Given
        val input = "<div><span>Test</span></div>"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("Test")
    }

    @Test
    fun `cleanHtml removes self-closing tags`() {
        // Given
        val input = "Test<br/>Content"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("TestContent")
    }

    @Test
    fun `cleanHtml removes tags with attributes`() {
        // Given
        val input = "<div class=\"test\">Content</div>"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("Content")
    }

    @Test
    fun `cleanHtml removes tags with complex attributes`() {
        // Given
        val input = "<div style=\"display: block; border-width: 0;\">Content</div>"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("Content")
    }
    //endregion

    //region cleanHtml - Bullet Point Formatting
    @Test
    fun `cleanHtml normalizes bullet point spacing`() {
        // Given
        val input = "* \nItem"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("* Item")
    }

    @Test
    fun `cleanHtml handles multiple bullet points with newlines`() {
        // Given
        val input = "* \nFirst\n* \nSecond"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("* First\n* Second")
    }

    @Test
    fun `cleanHtml handles bullet points with extra spaces`() {
        // Given
        val input = "*   \nItem"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("* Item")
    }
    //endregion

    //region cleanHtml - Whitespace Handling
    @Test
    fun `cleanHtml trims leading whitespace`() {
        // Given
        val input = "   Test"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("Test")
    }

    @Test
    fun `cleanHtml trims trailing whitespace`() {
        // Given
        val input = "Test   "

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("Test")
    }

    @Test
    fun `cleanHtml trims both leading and trailing whitespace`() {
        // Given
        val input = "   Test   "

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("Test")
    }

    @Test
    fun `cleanHtml preserves internal whitespace`() {
        // Given
        val input = "Test Content"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("Test Content")
    }
    //endregion

    //region cleanHtml - Real World Examples
    @Test
    fun `cleanHtml handles yoshimitsu notes into a list`() {
        // Given
        val input = """
        &lt;div class=&quot;plainlist&quot;&gt;
        * Floor Break
        * Weapon
        &lt;/div&gt;
    """
            .trimIndent()

        // When
        val cleaned = input.cleanHtml()
        val notes = cleaned
            .lines()
            .filter { it.isNotEmpty() }
            .map { it.removePrefix("* ").trim() }
            .filter { it.isNotEmpty() }

        // Then
        assertThat(notes).hasSize(2)
        assertThat(notes[0]).isEqualTo("Floor Break")
        assertThat(notes[1]).isEqualTo("Weapon")
    }

    @Test
    fun `cleanHtml handles complex yoshimitsu move note`() {
        // Given
        val input = """
            &lt;div class=&quot;plainlist&quot;&gt;
            * 
            &lt;div
              style=&quot;display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;&quot;
              class=&quot;movedata-icon border-blue homing&quot;
            &gt;Homing&lt;/div&gt;
            * Throw break 1 or 2&lt;/div&gt;
        """.trimIndent()

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result.contains("Homing")).isEqualTo(true)
        assertThat(result.contains("Throw break 1 or 2")).isEqualTo(true)
    }
    //endregion

    //region cleanHtml - Edge Cases
    @Test
    fun `cleanHtml handles empty string`() {
        // Given
        val input = ""

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("")
    }

    @Test
    fun `cleanHtml handles only whitespace`() {
        // Given
        val input = "   "

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("")
    }

    @Test
    fun `cleanHtml handles string with no HTML`() {
        // Given
        val input = "Plain text"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("Plain text")
    }

    @Test
    fun `cleanHtml handles unclosed tags`() {
        // Given
        val input = "<div>Test"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("Test")
    }
    //endregion

    //region cleanHtml - HTML Entity Decoding
    @Test
    fun `cleanHtml decodes numeric apostrophe entity`() {
        // Given
        val input = "Can&#039;t Heat Dash"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("Can't Heat Dash")
    }

    @Test
    fun `cleanHtml decodes named apostrophe entity`() {
        // Given
        val input = "Can&apos;t Heat Dash"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("Can't Heat Dash")
    }

    @Test
    fun `cleanHtml decodes less than entity`() {
        // Given
        val input = "Damage &lt;10"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("Damage <10")
    }

    @Test
    fun `cleanHtml decodes greater than entity`() {
        // Given
        val input = "Damage &gt;50"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("Damage >50")
    }

    @Test
    fun `cleanHtml decodes quote entity`() {
        // Given
        val input = "Move &quot;description&quot;"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("Move \"description\"")
    }

    @Test
    fun `cleanHtml decodes ampersand entity`() {
        // Given
        val input = "Fast &amp; powerful"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("Fast & powerful")
    }

    @Test
    fun `cleanHtml decodes non-breaking space entity`() {
        // Given
        val input = "Word&nbsp;spacing"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("Word spacing")
    }

    @Test
    fun `cleanHtml decodes multiple entities in one string`() {
        // Given
        val input = "Can&#039;t use &quot;special&quot; moves &amp; combos"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("Can't use \"special\" moves & combos")
    }

    @Test
    fun `cleanHtml handles both tags and entities`() {
        // Given
        val input = "<div>Can&#039;t block</div>"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result).isEqualTo("Can't block")
    }

    @Test
    fun `cleanHtml handles entities in complex HTML`() {
        // Given
        val input = "<div class=\"plainlist\">\n* Can&#039;t Heat Dash\n* Won&#039;t track</div>"

        // When
        val result = input.cleanHtml()

        // Then
        assertThat(result.contains("Can't Heat Dash")).isTrue()
        assertThat(result.contains("Won't track")).isTrue()
    }

    @Test
    fun `removeAccents handles Spanish characters`() {
        // Given
        val input = "ramón"

        // When
        val result = input.removeAccents()

        // Then
        assertThat(result).isEqualTo("ramon")
    }
    //endregion

    //region maskSecret
    @Test
    fun `maskSecret handles strings with four chars`() {
        //given
        val string = "abcd"
        val expected = "a**d"

        //when
        val result = string.maskSecret()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `maskSecret handles strings with eight chars`() {
        //given
        val string = "abcdabcx"
        val expected = "a******x"

        //when
        val result = string.maskSecret()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `maskSecret handles long strings`() {
        //given
        val string = "AM5/AM4/LGA1700/LGA18151"
        val expected = "AM5/****************8151"

        //when
        val result = string.maskSecret()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `maskSecret handles empty string`() {
        //given
        val string = ""
        val expected = ""

        //when
        val result = string.maskSecret()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `maskSecret handles single character`() {
        //given
        val string = "a"
        val expected = "*"

        //when
        val result = string.maskSecret()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}