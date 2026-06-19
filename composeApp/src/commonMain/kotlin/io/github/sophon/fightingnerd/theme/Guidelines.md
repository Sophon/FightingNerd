# FightingNerd UI Guideline

A clone of [STRV's brand system](https://brand.strv.com/), adapted for FightingNerd's UI.
This document covers **rules and use cases**. Actual color codes and typography values live in Kotlin code (see `core/ui/theme/` — TBD).

---

## Quick reference

| | |
|---|---|
| Display font | **Anton** (ALL CAPS, headlines) |
| UI font | **Inter** (body, labels, supporting headlines) |
| Background (dark) | Near-black (`Neutral 3%`) |
| Background (light) | Near-white (`Neutral 95%`) |
| Accent | Brand red — sparingly |
| Canonical mode | Dark |

---

## Color

### Palette structure

One accent (red) on a neutral grayscale ramp. No secondary or tertiary accent colors. System colors (green / yellow / orange) exist **only** for status feedback, never for decoration.

**Token families:**
- `Red` — brand accent + hover/pressed states
- `Neutral` — `3%` (near-black) through `95%` (near-white), 10 stops
- `White` / `Black` — pure
- System — Green (confirmations), Yellow (alerts), Orange (errors)

### The space allocation rule (most important)

Per screen, the visual budget is roughly:

1. **Most space** → background
2. **Next most** → text on background
3. **Cherry on top** → red

If a screen looks "red-heavy," it's wrong. Pull red back.

### Where each color goes — dark mode (canonical)

**Backgrounds (from lowest to highest elevation):**
| Surface | Color |
|---|---|
| App background | Neutral 3% |
| Elevated surface (card, sheet, list row container) | Neutral 10% |
| Higher elevation (dialog, popup, menu) | Neutral 15% |
| Highest / pressed states | Neutral 20% |

**Text on dark backgrounds:**
| Role | Color |
|---|---|
| Primary text (headlines, body) | White |
| Secondary text (subtitles, supporting info) | Neutral 65% |
| Tertiary text (timestamps, metadata, hints) | Neutral 40% |
| Disabled text | Neutral 40% |

**Dividers & borders:**
| Role | Color |
|---|---|
| Strong divider / focused outline | Neutral 40% |
| Subtle divider / unfocused outline | Neutral 20% |

### Where each color goes — light mode

Mirror the dark scheme. Light mode is supported but not canonical.

**Backgrounds:**
| Surface | Color |
|---|---|
| App background | Neutral 95% |
| Elevated surface | White |
| Higher elevation | Neutral 90% |

**Text on light backgrounds:**
| Role | Color |
|---|---|
| Primary text | Black |
| Secondary text | Neutral 40% |
| Tertiary text | Neutral 65% |

### Red — explicit usage list

Red appears **only** in these places. Anywhere else is wrong unless added to this list.

**Use red for:**
- The FightingNerd logo
- Primary CTA buttons (the single most important action on a screen)
- Selected / active state on primary navigation (bottom nav, tabs)
- High-importance move properties: **Heat**, **Homing**, and any future property of similar weight
- Focused outline on text fields (optional — neutral also valid)

**Do not use red for:**
- Ordinary body or heading text
- Dividers, borders, outlines (use neutrals)
- Backgrounds of any kind (no red surfaces, no red banners)
- Hover/pressed states on non-CTA buttons
- Secondary actions
- Decorative accents (no red dots, lines, or shapes "just because")
- Error states — use **Orange** instead

### System colors

| Color | Use for |
|---|---|
| Green | Success confirmations only (toasts, checkmarks) |
| Yellow | Warning states (non-blocking alerts) |
| Orange | Error states, destructive action confirmation, blocking alerts |

System colors are never decorative. If they're not communicating status, they shouldn't be on the screen.

### Don'ts

- Don't introduce new accent colors. One red is enough.
- Don't use gradients. Flat fills only.
- Don't use semi-transparent red over photos or surfaces.
- Don't use red as a "warning" — that's Orange's job. Red is brand.

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

Anton is **always ALL CAPS** and uses **−2% letter spacing**. Never sentence case. Never used for sentences or paragraphs.

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

### Type scale (roles, not pixel sizes)

Sizes get defined later in code. For now, these are the **roles** the scale needs to cover:

| Role | Font | Casing | Where it appears |
|---|---|---|---|
| Display Large | Anton | UPPER | Splash, onboarding hero, marketing-y empty states |
| Display | Anton | UPPER | Screen titles in large top app bar |
| Headline | Anton | UPPER | Section headers within a screen |
| Sub-headline | Anton | UPPER | Card titles, dialog titles |
| Title | Inter Bold | Sentence | Top app bar small title, list row primary text (when bolded) |
| Body Large | Inter Regular | Sentence | Body paragraphs |
| Body | Inter Regular | Sentence | Default body, list row secondary text |
| Label | Inter Medium | Sentence | Buttons, chips, tags |
| Label Small | Inter Medium | UPPER | Subtitle eyebrows, all-caps subtitles (use 5% letter spacing) |
| Caption | Inter Regular | Sentence | Timestamps, metadata, hints |
| Mono | Inter (use Inter, skip mono for now) | — | Reserved if needed later for frame data alignment |

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

4dp grid. All spacing should be a multiple of 4 (4, 8, 12, 16, 20, 24, 32, 40, 48, 64).

### Common spacing roles (rough guidance, to be tokenized later)

| Role | Approx |
|---|---|
| Tight (within a component) | 4–8 dp |
| Default (between related elements) | 12–16 dp |
| Section gap (between unrelated groups) | 24–32 dp |
| Screen padding (horizontal) | 16–24 dp |
| Screen padding (vertical, top of section) | 32–48 dp |

### Corners

**Sharp.** Use sharp corners for boxes, buttons, cards, charts, and other components. No rounded corners by default. Exceptions:
- Floating action elements (if any) — small radius acceptable
- Avatars / character images — circular when appropriate

### Layout principles

- One primary CTA per screen. Two only if both are equally weighted (rare).
- Less is more. If a screen feels cluttered, remove something before adding density.
- Sharp alignment. Things line up on the grid or they don't appear.

---

## Components

### Buttons

| Type | Background | Text | When |
|---|---|---|---|
| Primary CTA | Red | White | The single most important action on a screen |
| Secondary | Neutral 10% (dark) / Neutral 90% (light) | White (dark) / Black (light) | Common actions |
| Outlined | Transparent, border Neutral 40% | White (dark) / Black (light) | Tertiary actions |
| Text-only | Transparent | White (dark) / Black (light) | Inline / low-emphasis actions |
| Destructive | Orange | White | Delete, irreversible actions |

**Button rules:**
- Sharp corners.
- Label: Inter Medium or SemiBold.
- Casing: sentence case for actions ("Save changes"), ALL CAPS only when intentionally striking (rare).
- Primary CTA: max one per screen.
- Hover/pressed states defined per token in code (Red Hover, Red Pressed for primary; neutral shift for others).

### Text fields

- Background: Neutral 10% (dark) / White (light)
- Border (unfocused): Neutral 20%
- Border (focused): Neutral 40% — red is optional but not default
- Label: Inter Medium, Neutral 65% when unfocused, White when focused
- Helper / error text: Inter Regular Caption size; Orange for errors

### Cards & list rows

- Background: Neutral 10% on a Neutral 3% screen
- Padding: 16dp default
- Primary text: Inter SemiBold, White
- Secondary text: Inter Regular, Neutral 65%
- Metadata / tertiary: Inter Regular Caption, Neutral 40%
- Dividers between list rows: Neutral 20%, or no divider if cards are visually separated by spacing

### Top app bar

- Background: Neutral 3% (matches screen, no elevation)
- Large title (when scrolled to top): Anton, ALL CAPS, White
- Small title (when collapsed): Inter Bold, sentence case, White
- Back arrow & action icons: White, Neutral 65% when disabled

### Bottom navigation (if used)

- Background: Neutral 3% with subtle top divider (Neutral 20%)
- Icon: Neutral 65% (unselected), Red (selected)
- Label: Inter Medium, Neutral 65% (unselected), White (selected)
- Selected state uses red on the icon, not the label background

### Property tags / chips

The main place red appears in regular UI. Tags display move properties (Heat, Homing, Tornado, etc.).

| Property importance | Background | Text |
|---|---|---|
| High (Heat, Homing) | Red | White |
| Standard | Neutral 15% | White |
| Subtle / informational | Transparent, border Neutral 20% | Neutral 65% |

- Sharp corners.
- Small padding (4–8 dp vertical, 8–12 dp horizontal).
- Label: Inter Medium or SemiBold, often ALL CAPS Label Small role.

### Dialogs / sheets

- Background: Neutral 15%
- Title: Anton, ALL CAPS
- Body: Inter Regular
- Buttons: standard button rules (one primary CTA max)
- Dismiss: text-only button or X icon

---

## Iconography

### Style

- **Vector only** (SVG / vector drawables / SF Symbol-style line icons). No raster.
- **Simple, single-color**, designed to be tinted.
- **Consistent stroke weight** across the icon set (pick one — 1.5dp or 2dp typically — and stick to it).
- **Outline preferred** over filled, unless the icon represents a selected/active state.

### Sizes

| Use | Size |
|---|---|
| Inline with text | 16 dp |
| Default | 24 dp |
| Large (empty state, hero) | 48 dp |

### Tinting

- Default: White (dark mode) / Black (light mode)
- Disabled: Neutral 40%
- Selected / active: Red (only for nav / toggle states)
- Within a Red CTA button: White

### Imagery

- **Avoid imagery in UI chrome.** Backgrounds stay flat.
- The only imagery in the app is **character images** and **game logos**, which are externally sourced and out of design control.
- When character images appear, treat them as content (no overlays, no tints applied to brand them).

---

## Deferred / TODO

These are intentionally not covered yet. Decide when they become relevant.

- **Frame data semantic colors** — plus on block, minus on block, launcher, knockdown, etc. Likely needs its own sub-system separate from brand.
- **Motion / animation conventions** — durations, easings, transition patterns.
- **Touch target sizes** — accessibility minimums.
- **Contrast ratios** — formal accessibility audit.
- **Density variant for frame data tables** — tighter spacing than general UI.
- **Skeleton / loading states** — placeholder color and animation rules.
- **Pull-to-refresh, swipe actions, gestures** — interaction polish patterns.
- **Toast / overlay styling** — pending overlay infrastructure work.

---

## Source of truth split

- **Rules and use cases** → this document (GitHub wiki)
- **Color values, type sizes, spacing tokens** → Kotlin code in `core/ui/theme/` (TBD)
- When in doubt about a *rule*, check here. When in doubt about a *value*, check the code.
