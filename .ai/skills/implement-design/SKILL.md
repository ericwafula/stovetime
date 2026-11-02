---
name: implement-design
description: Build Stovetime UI exactly from the Figma file (page "v4 — Home cook") with the :presentation:design system — changing a token, adding a design-system component, and building a screen from its Figma frame, then checking the result against the frame.
when_to_use: Any roadmap step that adds or changes UI; "build the <screen> screen", "match the Figma", "add a design-system component", "add a token", or when UI code uses Material components, raw colours, fonts or sizes instead of StovetimeTheme tokens.
---

# Skill: implement-design

The Figma file is the contract. You build what it shows; you don't redesign.

## Read first (every time)

1. The design system in `presentation/design/src/main/java/com/ericwathome/stovetime/design/`: `theme/` (`StovetimeTheme`, `StovetimeColors`, `StovetimeTypography`, `StovetimeDimens.kt`, `Palette`) and every file in `components/`. Know the real signatures before you use them.
2. The Figma frame for the work, through the Figma MCP. Load the `figma-design-to-code` skill first (required before `get_design_context`), then call `get_design_context` with file key `4hJuAVOMHyuF0hEXxbSvlN` and the frame's node id. Use `get_screenshot` to see it and `get_variable_defs` for token values.
3. The matching component on the Figma **Components** page (Button, PathNode, StatChip, TimerChip, Progress) when the work touches one.

Frames on page "v4 — Home cook":

| Frame | Node id | Link |
|---|---|---|
| Home — Path | `70:98` | `https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=70-98` |
| Recipe — Intro | `72:120` | `https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=72-120` |
| Cook — Step | `72:158` | `https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=72-158` |
| Cook — Done | `72:184` | `https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=72-184` |
| Recipes — Browse | `80:48` | `https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=80-48` |
| Timers | `80:110` | `https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=80-110` |
| Timers — Quick timer | `81:194` | `https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=81-194` |
| Timer — Alarm | `81:178` | `https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=81-178` |
| Me | `81:73` | `https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=81-73` |
| Photos | `79:141` | `https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=79-141` |

A link's `node-id=70-98` is node id `70:98`. The code `get_design_context` returns is a reference to translate into the design system, not code to paste.

## Job A: add or change a token

1. Find the value in Figma (`get_variable_defs`). If Figma doesn't have it, **stop** and ask; never invent a colour, font or size.
2. Hex values live only in `theme/Palette.kt`, named after the ingredient (`Tomato500`, `Basil700`, `Yolk100`). Screens never see `Palette`.
3. Colours reach code as one of the roles in `StovetimeColors` (`canvas`, `surface`, `sunken`, `border`, `scrim`, `textPrimary`, `textSecondary`, `onAccent`, `onXp`, `primary`/`primaryShade`/`primarySoft`/`primaryContent`, `success`/`successShade`/`successSoft`/`successContent`, `xp`/`xpShade`/`xpSoft`/`xpContent`, `lockedFill`, `lockedContent`), set in both `LightColors` and `DarkColors`. `*Shade` is only for ledges; accent-coloured text and icons on a plain or soft background use `*Content`; text on a yolk face uses `onXp`. Prefer an existing role; add a role only when the meaning is new, and give it a dark value that passes `StovetimeColorsTest`.
4. Type styles are in `StovetimeTypography` (`display`, `title`, `heading`, `number`, `body`, `bodyStrong`, `label`, `caption`, `button`), built from the bundled Gabarito and Rubik variable fonts in `presentation/design/src/main/res/font/`.
5. Sizes: `StovetimeTheme.spacing` (`s4`, `s8`, `s12`, `s16`, `s20`, `s24`, `s32`), `StovetimeTheme.shapes` (`small`, `medium`, `large`, `full`), and `StovetimeLedge` (`Small`, `Regular`, `Large`) for the ledge under raised surfaces.

## Job B: add or change one component

1. Find it on the Figma Components page or in a v4 frame. If it's not there, **stop**: return `Needs input` with a proposed design (anatomy, sizes, states, tokens), and don't build it until the user approves it and it's in Figma.
2. Put it in `presentation/design/src/main/java/com/ericwathome/stovetime/design/components/`, named `Stove<Name>` for new components (the existing `PathNode`, `StatChip`, `TimerChip` and `Pill` keep their names).
3. Build on foundation and the design system, not Material: `StoveText` (over `BasicText`), `StoveIcon` with a `StoveIcons` entry (new icons are vector drawables in `presentation/design/src/main/res/drawable/ic_<name>.xml`), and `LedgeSurface` for anything raised or pressable.
4. Tokens only through `StovetimeTheme`. No `Color(0x…)` outside `Palette.kt`. Component-specific dimensions copied from Figma (a node's 76 × 82 dp, a 5 dp ring) may be `dp` literals inside the component file, or named `private val` constants when used more than once.
5. Ledge and press-in come from `LedgeSurface` with a `StovetimeLedge` height, never hand-rolled offsets or shadows.
6. Every component gets a preview showing every variant and state, rendered light and dark (`@PreviewLightDark` around `StovetimePreviewTheme { … }`, like `StoveButtonPreview`; never a hardcoded `backgroundColor`), and, where it has behaviour, a Compose UI test.

## Job C: build a screen from its frame

1. Find the step in `.ai/roadmap/backlog/`. Its **Design:** line names the Figma frame.
2. Build the ViewModel and screen with `add-viewmodel` + `new-compose-screen`. What the frame shows becomes the starting `State`; every tap target becomes an `Action`.
3. Lay out top to bottom following the frame's layers, using design-system components. Frame spacing in dp snaps to `StovetimeTheme.spacing`.
4. Sections whose data comes from a later step are **left out**, not stubbed with fake data (e.g. the weekly streak before progress is stored).
5. Strings: take the copy from the frame's text layers into the feature's `values/strings.xml`. Numbers that change (XP, minutes, step 2 of 5) are format arguments, not part of the string. If the copy reads like a placeholder, flag it; `humanizer` (UI copy mode) can propose wording.
6. Previews: one per state (loading, content, empty, error, and for cook mode first step, middle step, last step, timer running).
7. Accessibility: merged descriptions for grouped content (a path node reads "Jammy eggs, up next"), roles on custom clickables, a live region for a running timer's countdown, and content descriptions from `strings.xml` for `StatChip`, `PathNode` and icon-only actions.

## Verify against the frame (before `checks`)

- [ ] `get_screenshot` of the frame next to the screen's previews: same sections, order, components and states
- [ ] No Material components or `MaterialTheme` in the screen
- [ ] No raw colours, fonts or dp/sp literals outside `:presentation:design` (grep the diff for `Color(0x`, `.sp`, `FontFamily`, `.dp`)
- [ ] No string literals for `text`/`title`/`description`/`contentDescription` outside previews (`checkHardcodedText` enforces it)
- [ ] Every interactive element ≥ 48 dp touch area; icon-only buttons have content descriptions
- [ ] Raised and pressable surfaces use `LedgeSurface` (through the components), not hand-rolled offsets
- [ ] Previews exist for every state and render at 1.5× font scale without clipping
- [ ] With animations turned off, the screen shows end states (progress bar filled, ledge at rest)

Then run `checks`. Report what you built, which parts of the frame are covered, and anything the frame didn't answer (as `Needs input`, never a guess).

## Rules

- Precedence: the screen's Figma frame > the Figma Components page and variables > the existing design-system code. If code and Figma disagree, say so and ask which one is right.
- Light and dark follow the phone (`StovetimeTheme(darkTheme = isSystemInDarkTheme())`); there's no in-app switch. Figma has light frames only, so the dark values live in `DarkColors` (see the decision log) and every colour must come through a token so it switches. `MainActivity` re-applies `SystemBarStyle.auto(…)` and the `stovetime_canvas` window background when the theme changes, and handles `uiMode` itself so nothing is recreated. The alarm screen always uses `StovetimeTheme(darkTheme = false)`: red with white text.
- Don't port Figma's generated code line by line. It's a visual reference, not code.
- **Search screens keep results visible while typing.** When a search field is focused and the keyboard is open (`isKeyboardOpen` from `presentation/ui/.../helpers/KeyboardHelpers.kt`), collapse everything above the field that isn't needed to search (titles, the stats row, secondary actions) and hide bottom actions and the tab bar, so several recipes show between the field and the keyboard. Picking a result clears focus and hides the keyboard, which restores the full layout. Every search screen gets a Maestro flow that types with the keyboard open and asserts results are visible (`.maestro/flows/*/search_results_visible_with_keyboard.yaml`); don't use `hideKeyboard` after picking a result, since Maestro presses Back when no keyboard is open.
- **Bottom sheets** follow `add-launcher`: `StoveBottomSheet` is the only sheet primitive, in `:presentation:design`; every app sheet is a `remember[Name]Launcher` in `:presentation:ui/launchers/`, never a `*Sheet` in a feature.
- Recipe photos are TheMealDB images. Until an image loader exists, use the component's fallback (`PathNode` with `photo = null`) and never draw food art or illustrations yourself.
- If the user changes a design, the Figma file changes first (ask the user to update it, or confirm the change with them), then the code.
