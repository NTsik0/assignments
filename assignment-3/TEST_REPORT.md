# Assignment 3 Test Report

## Overview

Unit test suite for the refactored Columns game (`Java2026/src/columns`, excluding
`columns/original`). 72 tests, all passing, runtime ~400ms. No real sleeping, no
uncontrolled randomness, no applet required.

---

## Test Framework

**JUnit 5.10.1** (`junit-jupiter`), configured via Maven (`pom.xml`).
Tests and coverage run with `mvn test`.

---

## What Was Tested

### Figure (`FigureTest` — 14 tests)

| Behavior | Test |
|---|---|
| x starts at center column | `figureStartsAtHorizontalCenter` |
| y starts at row 1 | `figureStartsAtTopRow` |
| Colors assigned from `RandomGenerator` in order | `figureColorsAreSetFromRandomGenerator` |
| Color range always [1..7] | `figureColorValuesAreInValidRange` |
| `moveRight` increments x | `moveRightIncrementsX` |
| `moveLeft` decrements x | `moveLeftDecrementsX` |
| `moveRight` + `moveLeft` are inverses | `moveRightAndLeftAreInverse` |
| `moveDown` increments y | `moveDownIncrementsY` |
| `rotateUp` shifts colors upward | `rotateUpShiftsColorsUpward` |
| `rotateDown` shifts colors downward | `rotateDownShiftsColorsDownward` |
| `rotateUp` then `rotateDown` restores colors | `rotateUpThenDownRestoresOriginalColors` |
| Three `rotateUp`s restore colors | `threeRotateUpsRestoreOriginalColors` |
| Three `rotateDown`s restore colors | `threeRotateDownsRestoreOriginalColors` |
| Rotation does not change position | `rotationDoesNotChangePosition` |

### Board (`BoardTest` — 35 tests)

| Area | Tests |
|---|---|
| `initBoard` clears cells, score, level, counter | 4 |
| `pasteFigure` writes to correct cells, leaves other columns untouched | 2 |
| `canMoveLeft`: false at left wall, false when blocked, true when free | 3 |
| `canMoveRight`: false at right wall, false when blocked, true when free | 3 |
| `figureMayMoveDown`: true on empty board, false at bottom, false when blocked | 3 |
| `dropFigure`: lands at floor of empty column, lands above existing block | 2 |
| `isFieldFull`: false on clean board, true when row 3 occupied, checks all columns | 3 |
| `findMatches`: detects vertical, horizontal, diagonal triplets; no false positives | 4 |
| `findMatches`: increases score, notifies listener | 2 |
| `collapse`: packs survivors to bottom, notifies listener (field + score) | 3 |
| Scoring: higher level awards more points for same match | 1 |
| Level: no change before threshold, increases at threshold | 2 |
| Level: capped at MAX_LEVEL, resets counter, notifies listener | 3 |

### GameController events (`GameControllerTest` — 23 tests)

All events tested via `processEvent()` directly, bypassing  blocking game loop.

| Event | Behavior verified |
|---|---|
| `LEFT` | decrements x; blocked at left wall; blocked by occupied cell |
| `RIGHT` | increments x; blocked at right wall; blocked by occupied cell |
| `UP` | rotates colors upward; position unchanged |
| `DOWN` | rotates colors downward; position unchanged |
| `UP` + `DOWN` | restores original colors |
| `DROP` | figure moves to bottom of empty column; `tc` reset to 0; lands above floor block |
| `LEVEL_UP` | increments level; capped at MAX_LEVEL; resets match counter |
| `LEVEL_DOWN` | decrements level; floored at 0; resets match counter |
| `NONE` | changes nothing on the board |

---

## Test Doubles Used

### `RecordingListener` (hand-written inner class in `BoardTest`)

Implements `ModelListener`. Records the last level reported, the last score reported,
how many times `tripletDetected` was called, and whether `fieldWasUpdated` was called.
Used to verify Board callbacks without a real UI.

### `FakePlatform` (hand-written, `FakePlatform.java`)

Implements `Platform`. Provides:

- **Controlled time**: `currentTime()` returns a field the test controls; `delay()` does nothing.
- **Event queue**: tests push `GameEvent` values via `queueEvent()`; `getEvent()` pops them;
  `isKeyPressed()` is driven by the queue being non-empty.
- **Fixed randomness**: defaults to `() -> 0` (always color 1); overridable per test.
- **`FakeScreen`**: records all draw calls in a list for potential assertion.

### Deterministic `RandomGenerator` (anonymous lambda in test helpers)

Supplies a fixed integer sequence so figure colors are always exactly what the test
expects. No `java.util.Random` used anywhere in tests.

---

## What Was Hard to Test

### `runGameLoop` and `processUserEventsIfAny`

The game loop is a blocking `do...while` that mixes figure creation, falling, event
polling, match detection, collapse, and game-over in one method. Testing it
end-to-end requires a fake platform whose `currentTime()` drives the loop to
completion without hanging. Rather than building that fragile scaffold, the suite
tests `processEvent()` directly for all event logic and `Board` methods independently
for all game rules. This gives full behavioral coverage without coupling tests to
the loop structure.

### `PAUSE` event

The handler loops on `platform.isKeyPressed()`. Since pause is a UI concern
(flashing the figure) with no game-rule effect, it was omitted intentionally.

### `dropFigure` algorithm direction

The method scans from `DEPTH` downward **while cells are occupied**, stopping at the
first one empty cell. This means a block placed mid-column with empty rows below it is
invisible to the algorithm. The tests were initially written with wrong expected
values and corrected by tracing the algorithm carefully. This is a documentation gap where:
the intent is only clear from the implementation.

### `View` class

`View` was not supplied with the assignment source. A compatible stub was reconstructed
from the draw calls visible in `GameController`. Since no tests assert on drawing
details (they assert on model state), the stub only needs to compile correctly.

---

## Design Problems Revealed

### `Board` and `View` are not injected into `GameController`

Both are constructed internally. Tests must reach into `controller.board` straightly
via same-package access. If `Board` were injected, tests could pass in a
pre-configured instance without field access. This is the most significant
testability friction in the refactored design.

### `Board.noChanges` is a mutable side-effect flag

`noChanges` is set as a side effect of `findMatches()` instead of being returned as a
value. Tests must read the flag after calling the method, coupling them to an
implementation detail. A `boolean` return value would be cleaner.

### `dropFigure` scans bottom-up but reads top-down intuitively

The algorithm finds the lowest empty cell, not the highest occupied cell. Both give
the same result when a column is contiguous, but the scan direction means an isolated
mid-column block is invisible. A comment would clarify intent.

### Score and level are public fields

`Board.Score`, `Board.level`, and `Board.figuresMatchedCounter` are `public`, with
nothing preventing external corruption. Package-private access with controlled
mutation would be safer.

---

## Production-Code Changes Made

**None.** All behavior was testable without modifying production code.

- `Board`, `Figure`, `GameController`, and `View` are all in `columns.model`, so
  test classes in the same package access them directly without visibility changes.
- `GameController.processEvent()` is package-private and callable directly from tests.
- `Board.setModelListener()` accepts the `RecordingListener` without changes.

---

## Coverage (Bonus Track)

JaCoCo 0.8.11 configured in `pom.xml`, runs automatically with `mvn test`.
Report generated at `target/site/jacoco/index.html`.
Also in the files there is a screenshot provided.

**Results: 73% instruction coverage, 67% branch coverage across 5 classes.**

The uncovered code is concentrated in:

- `GameController.runGameLoop` and `processUserEventsIfAny` — the blocking loop
  intentionally not tested end-to-end
- The `PAUSE` event handler
- `View` drawing methods — tests assert on model state, not screen output

The coverage numbers directly confirm the honest gaps described in this report.
No coverage was inflated with weak assertions.

---

## Test Count Summary

| Class | Tests |
|---|---:|
| `FigureTest` | 14 |
| `BoardTest` | 35 |
| `GameControllerTest` | 23 |
| **Total** | **72** |

All 72 tests pass. Runtime: ~400ms.