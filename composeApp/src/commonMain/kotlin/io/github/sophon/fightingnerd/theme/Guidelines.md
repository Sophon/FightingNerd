# FightingNerd UI Guideline

A clone of [STRV's brand system](https://brand.strv.com/), adapted for FightingNerd's UI.
This document covers **rules and use cases**. Actual color codes, type sizes, and spacing values live in Kotlin code (see `core/ui/theme/`).

---

## Quick reference

| | |
|---|---|
| Display font | **Anton** (ALL CAPS, headlines) |
| UI font | **Inter** (body, labels, supporting headlines) |
| Background (dark) | Near-black |
| Background (light) | Near-white |
| Accent | Brand red — sparingly |
| Canonical mode | Dark |

---

## Color

### Palette structure

One accent (red) on a neutral grayscale ramp. No secondary or tertiary accent colors. System colors (success / warning / error) exist **only** for status feedback, never for decoration.

**Token families:**
- `accent` / `accentHover` / `accentPressed` — brand red + interaction states
- `background` — app background, the dominant color on every screen
- `surface` / `surfaceHigh` / `surfacePressed` — elevated neutrals
- `textPrimary` / `textSecondary` / `textTertiary` / `textDisabled` — text on background
- `divider` / `dividerSubtle` — separators and outlines
- `success` / `warning` / `error` — status feedback
- `scrim` — modal overlays (combined with alpha at call site)

### The space allocation rule (most important)

Per screen, the visual budget is roughly:

1. **Most space** → `background`
2. **Next most** → text on background
3. **Cherry on top** → `accent`

If a screen looks "red-heavy," it's wrong. Pull `accent` back.

### Where each color goes

**Backgrounds (from lowest to highest elevation):**
| Surface | Token |
|---|---|
| App background | `background` |
| Elevated surface (card, sheet, list row container) | `surface` |
| Higher elevation (dialog, popup, menu) | `surfaceHigh` |
| Pressed states on neutral surfaces | `surfacePressed` |

**Text on backgrounds:**
| Role | Token |
|---|---|
| Primary text (headlines, body) | `textPrimary` |
| Secondary text (subtitles, supporting info) | `textSecondary` |
| Tertiary text (timestamps, metadata, hints) | `textTertiary` |
| Disabled text | `textDisabled` |

**Dividers & borders:**
| Role | Token |
|---|---|
| Strong divider / focused outline | `divider` |
| Subtle divider / unfocused outline | `dividerSubtle` |

Light mode mirrors the same role structure. Light is supported but not canonical.

### `accent` — explicit usage list

`accent` is **reserved**. Use only for the cases below — anywhere else is wrong unless added to this list.

**Use `accent` for:**
- The FightingNerd logo
- Primary CTA buttons (the single most important action on a screen)
- Selected / active state on primary navigation (bottom nav, tabs)
- High-importance move properties: **Heat**, **Homing**, and any future property of similar weight

**Never use `accent` for:**
- Ordinary body or heading text
- Dividers, borders, outlines (use neutrals)
- Backgrounds of any kind (no red surfaces, no red banners)
- Hover/pressed states on non-CTA buttons
- Secondary actions
- Decorative accents (no red dots, lines, or shapes "just because")
- Error states — use `error` instead

`accentHover` and `accentPressed` are for interaction states on `accent` surfaces only (i.e. the primary CTA). They are not general-purpose neutrals.

### System colors

| Token | Use for |
|---|---|
| `success` | Confirmation feedback only (toasts, checkmarks) |
| `warning` | Non-blocking alerts |
| `error` | Error states, destructive action confirmation, blocking alerts |

System colors are never decorative. If they're not communicating status, they shouldn't be on the screen.

### Don'ts

- Don't introduce new accent colors. One red is enough.
- Don't use gradients. Flat fills only.
- Don't use semi-transparent red over photos or surfaces.
- Don't use `accent` as a "warning" — that's `error`'s job. `accent` is brand.

---

## Typography

### Two fonts

**Anton** — display workhorse. ALL CAPS only. Used for all headlines and sub-headlines.
**Inter** — UI font. Used for body, labels, buttons, supporting text, and any place that isn't a headline.

Anton is always **noticeably larger** than Inter when they appear together. That's how visual hierarchy holds.

### When to use Anton

Anton appears in:
- Screen titles (top app bar large title)
- Section headers within a screen
- Onboarding / splash / empty-state primary messages
- Card titles for hero / featured content
- "Display" moments: dialog headlines, prominent CTAs (button label optional — usually Inter is fine)

Anton is **always ALL CAPS** (uppercase at call site) and uses **−2% letter spacing**. Never sentence case. Never used for sentences or paragraphs.

### When to use Inter

Inter handles everything else:
- Body text (paragraphs, descriptions)
- List rows (move names, character names — these are NOT headlines, they're data)
- Labels (form labels, tags, chips)
- Buttons (CTA labels included)
- Top app bar non-title elements (back arrow tooltip, action labels)
- Bottom nav labels
- Frame data tables (all of it)
- Captions, timestamps, metadata

Inter weights used:
- **Black / ExtraBold** — strong emphasis within UI (selected nav label, active filter)
- **Bold** — sub-headings, button labels, important inline emphasis
- **SemiBold** — list row primary text, form labels
- **Medium** — body emphasis, secondary buttons
- **Regular** — default body text
- (Skip Light / Thin — too fragile on mobile)

### Type scale (M3 slot mapping)

| Role | M3 slot(s) | Font | Casing | Where it appears |
|---|---|---|---|---|
| Display | `displayLarge` / `displayMedium` / `displaySmall` | Anton | UPPER | Hero moments: splash, onboarding, marketing empty states |
| Headline | `headlineLarge` / `headlineMedium` | Anton | UPPER | Screen titles in large top app bar, section headers |
| Sub-headline | `headlineSmall` | Anton | UPPER | Card titles, dialog titles |
| Title | `titleLarge` / `titleMedium` / `titleSmall` | Inter Bold/SemiBold | Sentence | Top app bar small title, list row primary text |
| Body | `bodyLarge` / `bodyMedium` | Inter Regular | Sentence | Paragraphs, list row secondary text |
| Caption | `bodySmall` | Inter Regular | Sentence | Timestamps, metadata, hints |
| Label | `labelLarge` | Inter Medium | Sentence | Buttons, chips, tags |
| Label Small | `labelMedium` / `labelSmall` | Inter Medium | UPPER | Subtitle eyebrows, all-caps subtitles |

### Letter spacing

- **Anton (default)**: −2%
- **Inter ALL CAPS labels**: +5%
- **Inter sentence case**: 0% (default)

### Don'ts

- Don't use Anton in sentence case. Ever.
- Don't use Anton for body text or long strings.
- Don't use outlined text.
- Don't use "..." in titles — skip it, the meaning usually survives.
- Don't use tiny fonts. If it's hard to read, it shouldn't be there.
- Don't mix Inter weights randomly — pick one per role and stick with it.

---

## Spacing & Layout

### Approach

Copy STRV's "give it space to breathe" density for general UI. Frame data and move lists are exceptions — they're information-dense by nature and need their own density tuning later.

### Base unit

4dp grid. All spacing should be a multiple of 4.

### Spacing roles

Values live in code; the markdown documents what each role is *for*.

**Screen-level:**
| Token | Role |
|---|---|
| `screenPaddingHorizontal` | Left/right padding inside a screen |
| `screenPaddingVertical` | Top/bottom padding inside a screen |
| `sectionGap` | Between unrelated sections within a screen |
| `sectionGapLarge` | Between major sections (e.g. screen title → first content) |

**Component-level:**
| Token | Role |
|---|---|
| `componentPadding` | Default internal padding for cards, sheets, dialogs |
| `componentPaddingTight` | Tighter internal padding for compact components |
| `componentGap` | Between sibling components (button row, card-to-card) |
| `componentGapTight` | Between closely related sibling components |

**Inline:**
| Token | Role |
|---|---|
| `inlineGap` | Gap between icon and label, or two inline elements |
| `inlineGapTight` | Very tight inline pairings (badge + count) |
| `matrixGap` | Used for matrices (frame data grids etc.) |

**Component-specific paddings:**
| Token | Role |
|---|---|
| `buttonPaddingHorizontal` / `buttonPaddingVertical` | Inside buttons |
| `chipPaddingHorizontal` / `chipPaddingVertical` | Inside chips and tags |
| `listRowPaddingHorizontal` / `listRowPaddingVertical` | Inside list rows |
| `topAppBarPaddingHorizontal` | Inside the top app bar |
| `dialogPadding` | Inside dialogs |

### Corners

**Subtle rounding by default.** Use `cornerDefault` for buttons, cards, chips, sheets, dialogs, and other surfaces. Use `cornerExtra` only when a component genuinely needs more pronounced rounding (rare).

Exceptions:
- Avatars / character images — circular when appropriate
- Dividers, borders — no radius (they're lines)

### Strokes

| Token | Role |
|---|---|
| `strokeThin` | Dividers, default borders |
| `strokeStrong` | Focused outlines, emphasized borders |

### Heights

| Token | Role |
|---|---|
| `topAppBarHeight` | Top app bar |
| `bottomNavHeight` | Bottom nav bar |
| `buttonHeight` | Default button height |
| `buttonHeightCompact` | Compact button variant |
| `minTouchTarget` | Accessibility minimum for any interactive element |

### Layout principles

- One primary CTA per screen. Two only if both are equally weighted (rare).
- Less is more. If a screen feels cluttered, remove something before adding density.
- Grid alignment. Things line up on the 4dp grid or they don't appear.

---

## Components

### Buttons

| Type | Background | Text | When |
|---|---|---|---|
| Primary CTA | `accent` | `textPrimary` on red | The single most important action on a screen |
| Secondary | `surface` | `textPrimary` | Common actions |
| Outlined | Transparent, border `divider` | `textPrimary` | Tertiary actions |
| Text-only | Transparent | `textPrimary` | Inline / low-emphasis actions |
| Destructive | `error` | `textPrimary` on orange | Delete, irreversible actions |

**Button rules:**
- `cornerDefault` corners.
- Label: Inter Medium or SemiBold (`labelLarge` / `titleSmall`).
- Casing: sentence case for actions ("Save changes"), ALL CAPS only when intentionally striking (rare).
- Primary CTA: max one per screen.
- Hover/pressed states: `accentHover` / `accentPressed` on the primary CTA; `surfacePressed` for neutral buttons.

### Text fields

- Background: `surface`
- Border (unfocused): `dividerSubtle`
- Border (focused): `divider` (neutral — `accent` is not used on text field outlines)
- Label: Inter Medium, `textSecondary` when unfocused, `textPrimary` when focused
- Helper / error text: Caption size (`bodySmall`); `error` for errors

### Cards & list rows

- Background: `surface` on a `background` screen
- Padding: `componentPadding` default, `listRowPadding*` for list rows
- Primary text: Inter SemiBold (`titleMedium`), `textPrimary`
- Secondary text: Inter Regular (`bodyMedium`), `textSecondary`
- Metadata / tertiary: Caption (`bodySmall`), `textTertiary`
- Dividers between list rows: `dividerSubtle`, or no divider if cards are visually separated by spacing
- Corners: `cornerDefault`

### Top app bar

- Background: `background` (matches screen, no elevation)
- Large title (when scrolled to top): Anton ALL CAPS, `textPrimary` (`headlineLarge`)
- Small title (when collapsed): Inter Bold sentence case, `textPrimary` (`titleLarge`)
- Back arrow & action icons: `textPrimary`, `textDisabled` when disabled

### Bottom navigation

- Background: `background` with subtle top divider (`dividerSubtle`)
- Icon: `textSecondary` (unselected), `accent` (selected)
- Label: Inter Medium (`labelLarge`), `textSecondary` (unselected), `textPrimary` (selected)
- Selected state uses `accent` on the icon, not the label background

### Property tags / chips

The main place `accent` appears in regular UI. Tags display move properties (Heat, Homing, Tornado, etc.).

| Property importance | Background | Text |
|---|---|---|
| High (Heat, Homing) | `accent` | `textPrimary` on red |
| Standard | `surfaceHigh` | `textPrimary` |
| Subtle / informational | Transparent, border `dividerSubtle` | `textSecondary` |

- Corners: `cornerDefault`.
- Padding: `chipPaddingHorizontal` / `chipPaddingVertical`.
- Label: Inter Medium, often ALL CAPS Label Small role (`labelMedium`).

### Dialogs / sheets

- Background: `surfaceHigh`
- Padding: `dialogPadding`
- Title: Anton ALL CAPS (`headlineSmall`)
- Body: Inter Regular (`bodyMedium`)
- Buttons: standard button rules (one primary CTA max)
- Dismiss: text-only button or X icon
- Corners: `cornerDefault`

---

## Iconography

### Style

- **Vector only** (SVG / vector drawables / SF Symbol-style line icons). No raster.
- **Simple, single-color**, designed to be tinted.
- **Consistent stroke weight** across the icon set (pick one and stick to it).
- **Outline preferred** over filled, unless the icon represents a selected/active state.

### Sizes

| Token | Use |
|---|---|
| `iconInline` | Alongside text |
| `iconDefault` | Top bar, buttons, list rows |
| `iconLarge` | Empty states, hero moments |
| `iconHeadline` | With headlines |

### Tinting

- Default: `textPrimary`
- Disabled: `textDisabled`
- Selected / active: `accent` (only for nav / toggle states)
- Within an `accent` CTA button: `textPrimary` on red

### Imagery

- **Avoid imagery in UI chrome.** Backgrounds stay flat.
- The only imagery in the app is **character images** and **game logos**, which are externally sourced and out of design control.
- When character images appear, treat them as content (no overlays, no tints applied to brand them).

---

## Deferred / TODO

These are intentionally not covered yet. Decide when they become relevant.

- **Frame data semantic colors** — plus on block, minus on block, launcher, knockdown, etc. Likely needs its own sub-system separate from brand.
- **Motion / animation conventions** — durations, easings, transition patterns.
- **Contrast ratios** — formal accessibility audit. (`minTouchTarget` token exists; contrast still pending.)
- **Density variant for frame data tables** — tighter spacing than general UI.
- **Skeleton / loading states** — placeholder color and animation rules.
- **Pull-to-refresh, swipe actions, gestures** — interaction polish patterns.
- **Toast / overlay styling** — pending overlay infrastructure work.

---

## Source of truth split

- **Rules and use cases** → this document
- **Color values, type sizes, spacing tokens** → Kotlin code in `core/ui/theme/`
- When in doubt about a *rule*, check here. When in doubt about a *value*, check the code.