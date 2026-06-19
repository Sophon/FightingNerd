package io.github.sophon.fightingnerd.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.anton
import fightingnerd.composeapp.generated.resources.inter_black
import fightingnerd.composeapp.generated.resources.inter_bold
import fightingnerd.composeapp.generated.resources.inter_extra_bold
import fightingnerd.composeapp.generated.resources.inter_medium
import fightingnerd.composeapp.generated.resources.inter_regular
import fightingnerd.composeapp.generated.resources.inter_semi_bold
import org.jetbrains.compose.resources.Font

/**
 * `Anton` - ALL CAPS only, −2% letter spacing. Uppercase at the call site:
 * `text.uppercase()`. Do NOT use for: sentence case, body, list rows, button
 * labels, long strings.
 *
 * `Inter` - sentence case by default. ALL CAPS only for eyebrow labels
 * (+5% spacing). Do NOT use for: display headlines, italics, Light/Thin weights.
 *
 * General: no outlined text, no "..." in titles, no tiny fonts.
 *
 * Roles:
 * - `Display` - `Anton, ALL CAPS`. Hero moments: splash, onboarding, marketing empty states.
 * - `Headline` - `Anton, ALL CAPS`. Screen titles in large top app bar, section headers.
 * - `Sub-headline` - `Anton, ALL CAPS`. Card titles, dialog titles.
 * - `Title` - `Inter Bold/SemiBold, sentence case`. Top app bar small title, list row primary text.
 * - `Body` - `Inter Regular, sentence case`. Paragraphs, list row secondary text.
 * - `Caption` - `Inter Regular, sentence case`. Timestamps, metadata, hints.
 * - `Label` - `Inter Medium, sentence case`. Buttons, chips, tags.
 * - `Label Small` - `Inter Medium, ALL CAPS`. Subtitle eyebrows, all-caps subtitles.
 */

@Composable
internal fun interFontFamily(): FontFamily {
    return FontFamily(
        Font(Res.font.inter_black, FontWeight.Black),
        Font(Res.font.inter_bold, FontWeight.Bold),
        Font(Res.font.inter_extra_bold, FontWeight.ExtraBold),
        Font(Res.font.inter_medium, FontWeight.Medium),
        Font(Res.font.inter_regular, FontWeight.Normal),
        Font(Res.font.inter_semi_bold, FontWeight.SemiBold),
    )
}

@Composable
internal fun antonFontFamily(): FontFamily {
    return FontFamily(
        Font(Res.font.anton, FontWeight.Normal)
    )
}

@Composable
internal fun fightingNerdTypography(): Typography {
    val inter = interFontFamily()
    val anton = antonFontFamily()

    val antonLetterSpacing = (-0.02).em
    val allCapsLabelSpacing = 0.05.em

    return Typography(
        displayLarge = TextStyle(
            fontFamily = anton,
            fontWeight = FontWeight.Normal,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = antonLetterSpacing,
        ),
        displayMedium = TextStyle(
            fontFamily = anton,
            fontWeight = FontWeight.Normal,
            fontSize = 45.sp,
            lineHeight = 52.sp,
            letterSpacing = antonLetterSpacing,
        ),
        displaySmall = TextStyle(
            fontFamily = anton,
            fontWeight = FontWeight.Normal,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = antonLetterSpacing,
        ),
        headlineLarge = TextStyle(
            fontFamily = anton,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = antonLetterSpacing,
        ),
        headlineMedium = TextStyle(
            fontFamily = anton,
            fontWeight = FontWeight.Normal,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = antonLetterSpacing,
        ),
        headlineSmall = TextStyle(
            fontFamily = anton,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = antonLetterSpacing,
        ),
        titleLarge = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp,
        ),
        titleSmall = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = allCapsLabelSpacing,
        ),
        labelSmall = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = allCapsLabelSpacing,
        ),
    )
}
