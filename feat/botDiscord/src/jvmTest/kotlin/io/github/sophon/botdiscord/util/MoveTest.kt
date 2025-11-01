package io.github.sophon.botdiscord.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.Test

class MoveTest {

    @Test
    fun `orClickable returns null when input is null`() {
        val input: String? = null
        val result = input.orClickable()
        assertThat(result).isNull()
    }

    @Test
    fun `orClickable returns original string when missing opening brackets`() {
        val input = "Kazuya combos#Staples|+59a]]"
        val result = input.orClickable()
        assertThat(result).isEqualTo(input)
    }

    @Test
    fun `orClickable returns original string when missing closing brackets`() {
        val input = "[[Kazuya combos#Staples|+59a"
        val result = input.orClickable()
        assertThat(result).isEqualTo(input)
    }

    @Test
    fun `orClickable returns original string when missing pipe`() {
        val input = "[[Kazuya combos#Staples]]"
        val result = input.orClickable()
        assertThat(result).isEqualTo(input)
    }

    @Test
    fun `orClickable returns original string when missing hash`() {
        val input = "[[Kazuya combos|+59a]]"
        val result = input.orClickable()
        assertThat(result).isEqualTo(input)
    }

    @Test
    fun `orClickable converts pure wiki link to markdown link`() {
        val input = "[[Kazuya combos#Staples|+59a]]"
        val expected = "[+59a](https://wavu.wiki/t/Kazuya_combos#Staples)"
        val result = input.orClickable()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `orClickable converts embedded wiki link with preceding text`() {
        val input = "Heat Dash +18g, [[Reina_combos#Mini-combos|+43a (+35)]]"
        val expected = "Heat Dash +18g, [+43a (+35)](https://wavu.wiki/t/Reina_combos#Mini-combos)"
        val result = input.orClickable()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `orClickable handles wiki link with spaces in page name`() {
        val input = "[[Kazuya combos#Staples|+59a]]"
        val expected = "[+59a](https://wavu.wiki/t/Kazuya_combos#Staples)"
        val result = input.orClickable()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `orClickable handles wiki link with underscores in page name`() {
        val input = "[[Reina_combos#Mini-combos|+43a]]"
        val expected = "[+43a](https://wavu.wiki/t/Reina_combos#Mini-combos)"
        val result = input.orClickable()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `orClickable handles complex display text with special characters`() {
        val input = "[[Devil_Jin_combos#EWGF|+43a (+35)]]"
        val expected = "[+43a (+35)](https://wavu.wiki/t/Devil_Jin_combos#EWGF)"
        val result = input.orClickable()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `orClickable returns original when string contains brackets but not as wiki link`() {
        val input = "Some text [not a wiki link]"
        val result = input.orClickable()
        assertThat(result).isEqualTo(input)
    }

    @Test
    fun `orClickable handles empty display text`() {
        val input = "[[Kazuya_combos#Staples|]]"
        val expected = "[](https://wavu.wiki/t/Kazuya_combos#Staples)"
        val result = input.orClickable()
        assertThat(result).isEqualTo(expected)
    }
}