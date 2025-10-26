package com.example.core.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import kotlin.test.Test

class StringExtensionsTest {

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
}