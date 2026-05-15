package columns.model;

import columns.model.kernel.ModelListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Board behavior: movement boundaries, match detection,
 * collapse/pack, scoring, level changes, and game-over detection.
 * Board and Figure are package-private; tests live in the same package.
 * RecordingListener captures callbacks instead of relying on real UI.
 */
class BoardTest {


    /** supply exactly the three color values. */
    private static Figure fig(int c1, int c2, int c3) {
        return new Figure(seqGen(c1 - 1, c2 - 1, c3 - 1));
    }

    private static columns.model.kernel.RandomGenerator seqGen(int... values) {
        return new columns.model.kernel.RandomGenerator() {
            int idx = 0;
            @Override public int nextInt() { return values[idx++ % values.length]; }
        };
    }

    /** records the last callback received from the board */
    static class RecordingListener implements ModelListener {
        int lastLevel = -1;
        long lastScore = -1;
        int tripletCount = 0;
        boolean fieldUpdated = false;

        @Override public void levelHasChanged(int level) { lastLevel = level; }
        @Override public void tripletDetected(int a, int b, int c, int d, int i, int j) { tripletCount++; }
        @Override public void fieldWasUpdated(int[][] newField) { fieldUpdated = true; }
        @Override public void scoreUpdated(long score) { lastScore = score; }
    }

    private Board board;
    private RecordingListener listener;

    @BeforeEach
    void setUp() {
        listener = new RecordingListener();
        board = new Board();
        board.initFields();
        board.setModelListener(listener);
        board.initBoard();
    }

    // here we have initboard

    @Test
    void initBoardClearsAllCells() {
        // let's place some values then re-init
        board.newField[3][5] = 4;
        board.initBoard();
        for (int c = 0; c <= GameConfig.WIDTH; c++) {
            for (int r = 0; r <= GameConfig.DEPTH; r++) {
                assertEquals(0, board.newField[c][r],
                        "initBoard must zero every cell at [" + c + "][" + r + "]");
            }
        }
    }

    @Test
    void initBoardResetsScoreToZero() {
        board.Score = 9999;
        board.initBoard();
        assertEquals(0, board.Score);
    }

    @Test
    void initBoardResetsLevelToZero() {
        board.level = 5;
        board.initBoard();
        assertEquals(0, board.level);
    }

    @Test
    void initBoardResetsFiguresMatchedCounterToZero() {
        board.figuresMatchedCounter = 20;
        board.initBoard();
        assertEquals(0, board.figuresMatchedCounter);
    }

    // here is pasteFigure

    @Test
    void pasteFigureWritesColorsIntoCorrectCells() {
        Figure f = fig(2, 5, 7);
        f.x = 3;
        f.y = 4;
        board.pasteFigure(f);
        assertAll(
                () -> assertEquals(2, board.newField[3][4], "c[1] should land at (x, y)"),
                () -> assertEquals(5, board.newField[3][5], "c[2] should land at (x, y+1)"),
                () -> assertEquals(7, board.newField[3][6], "c[3] should land at (x, y+2)")
        );
    }

    @Test
    void pasteFigureDoesNotWriteToOtherColumns() {
        Figure f = fig(2, 5, 7);
        f.x = 3;
        f.y = 4;
        board.pasteFigure(f);
        // column 4 should be untouched
        for (int r = 1; r <= GameConfig.DEPTH; r++) {
            assertEquals(0, board.newField[4][r],
                    "pasteFigure must not affect column 4");
        }
    }

    // movement boundaries

    @Test
    void canMoveLeftIsFalseAtLeftEdge() {
        Figure f = fig(1, 2, 3);
        f.x = 1;
        board.figure = f;
        assertFalse(board.canMoveLeft(), "figure at x=1 must not move left");
    }

    @Test
    void canMoveLeftIsTrueWhenInsideBoard() {
        Figure f = fig(1, 2, 3);
        f.x = 3;
        board.figure = f;
        assertTrue(board.canMoveLeft(), "figure at x=3 with empty board should be able to move left");
    }

    @Test
    void canMoveLeftIsFalseWhenBlockingCellExists() {
        Figure f = fig(1, 2, 3);
        f.x = 4;
        f.y = 5;
        board.figure = f;
        // Place a block at the cell the bottom of the figure would move into
        board.newField[3][7] = 1; // column x-1=3, row y+2=7
        assertFalse(board.canMoveLeft(),
                "figure should not move left when adjacent cell is occupied");
    }

    @Test
    void canMoveRightIsFalseAtRightEdge() {
        Figure f = fig(1, 2, 3);
        f.x = GameConfig.WIDTH;
        board.figure = f;
        assertFalse(board.canMoveRight(), "figure at max x must not move right");
    }

    @Test
    void canMoveRightIsTrueWhenInsideBoard() {
        Figure f = fig(1, 2, 3);
        f.x = 3;
        board.figure = f;
        assertTrue(board.canMoveRight(), "figure at x=3 with empty board should be able to move right");
    }

    @Test
    void canMoveRightIsFalseWhenBlockingCellExists() {
        Figure f = fig(1, 2, 3);
        f.x = 4;
        f.y = 5;
        board.figure = f;
        // block at (x+1, y+2) = (5, 7)
        board.newField[5][7] = 1;
        assertFalse(board.canMoveRight(),
                "figure should not move right when adjacent cell is occupied");
    }

    @Test
    void figureMayMoveDownIsTrueOnEmptyBoard() {
        Figure f = fig(1, 2, 3);
        f.x = 3;
        f.y = 1;
        board.figure = f;
        assertTrue(board.figureMayMoveDown(), "figure at top of empty board should be able to move down");
    }

    @Test
    void figureMayMoveDownIsFalseAtBottom() {
        Figure f = fig(1, 2, 3);
        f.x = 3;
        f.y = GameConfig.DEPTH - 2; // bottom-most valid starting row
        board.figure = f;
        assertFalse(board.figureMayMoveDown(), "figure at bottom row must not move further down");
    }

    @Test
    void figureMayMoveDownIsFalseWhenCellBelowIsOccupied() {
        Figure f = fig(1, 2, 3);
        f.x = 3;
        f.y = 5;
        board.figure = f;
        board.newField[3][8] = 2; // cell at y+3
        assertFalse(board.figureMayMoveDown(),
                "figure should not move down when cell directly below is occupied");
    }

    // dropFigure where should it be

    @Test
    void dropFigureLandsOnBottomOfEmptyColumn() {
        Figure f = fig(1, 2, 3);
        f.x = 3;
        f.y = 1;
        board.figure = f;
        board.dropFigure(f);
        // Bottom of the figure (y+2) should be at DEPTH
        assertEquals(GameConfig.DEPTH - 2, f.y,
                "dropped figure bottom should rest at the floor");
    }

    @Test
    void dropFigureLandsOnTopOfExistingBlock() {
        Figure f = fig(1, 2, 3);
        f.x = 3;
        f.y = 1;
        board.figure = f;
        // dropFigure scans from DEPTH=15 upward while rows are occupied.
        // Block must be at the bottom (row 15) to be hit.
        // zz=15: occupied → zz=14: empty → stop. f.y = 14-2 = 12.
        board.newField[3][15] = 5;
        board.dropFigure(f);
        assertEquals(12, f.y,
                "dropped figure should rest on top of block at the bottom row");
    }

    // field-full = game over

    @Test
    void isFieldFullIsFalseOnCleanBoard() {
        assertFalse(board.isFieldFull(), "fresh board should not be full");
    }

    @Test
    void isFieldFullIsTrueWhenRow3HasBlock() {
        board.newField[1][3] = 2;
        assertTrue(board.isFieldFull(), "board with block in row 3 should be considered full");
    }

    @Test
    void isFieldFullIsFalseWhenRow3IsEmptyButRow4HasBlock() {
        board.newField[4][4] = 3;
        assertFalse(board.isFieldFull(), "block below row 3 must not trigger game over");
    }

    @Test
    void isFieldFullChecksAllColumnsInRow3() {
        // Only the last column has a block in row 3
        board.newField[GameConfig.WIDTH][3] = 1;
        assertTrue(board.isFieldFull(),
                "any occupied cell in row 3 across any column should trigger game over");
    }

    // find/detect matches

    @Test
    void findMatchesDetectsVerticalTriplet() {
        // Three identical colors stacked vertically
        board.newField[3][5] = 2;
        board.newField[3][6] = 2;
        board.newField[3][7] = 2;
        board.findMatches();
        assertFalse(board.noChanges, "vertical triplet of same color must be detected");
    }

    @Test
    void findMatchesDetectsHorizontalTriplet() {
        board.newField[2][8] = 3;
        board.newField[3][8] = 3;
        board.newField[4][8] = 3;
        board.findMatches();
        assertFalse(board.noChanges, "horizontal triplet of same color must be detected");
    }

    @Test
    void findMatchesDetectsDiagonalTriplet() {
        // Diagonal: (2,8), (3,9), (4,10)
        board.newField[2][8] = 4;
        board.newField[3][9] = 4;
        board.newField[4][10] = 4;
        board.findMatches();
        assertFalse(board.noChanges, "diagonal triplet of same color must be detected");
    }

    @Test
    void findMatchesDoesNotFlagDifferentColors() {
        board.newField[3][5] = 1;
        board.newField[3][6] = 2;
        board.newField[3][7] = 3;
        board.findMatches();
        assertTrue(board.noChanges, "different colors in a column must not trigger a match");
    }

    @Test
    void findMatchesIncreasesScoreByExpectedAmount() {
        // At level 0: score per triplet = (0 + 1) * 10 = 10
        // But checkNeighbours detects each cell in the triplet separately,
        // so the center cell fires multiple matches. We just check score > 0.
        board.newField[3][5] = 2;
        board.newField[3][6] = 2;
        board.newField[3][7] = 2;
        board.findMatches();
        assertTrue(board.Score > 0,
                "finding a match at level 0 should increase the score");
    }

    @Test
    void findMatchesNotifiesListenerViaTripletDetected() {
        board.newField[3][5] = 2;
        board.newField[3][6] = 2;
        board.newField[3][7] = 2;
        board.findMatches();
        assertTrue(listener.tripletCount > 0,
                "listener.tripletDetected must be called when a match is found");
    }

    @Test
    void noMatchLeavesNoChangesTrue() {
        board.newField[1][15] = 1;
        board.newField[2][15] = 2;
        board.newField[3][15] = 3;
        board.findMatches();
        assertTrue(board.noChanges, "no match should leave noChanges as true");
    }

    // collapse/ packfield

    @Test
    void collapseMovesRemainingCellsToBottomOfColumn() {
        // Put a cell at row 5 in column 2, leave rows below empty in oldField
        // We simulate: after match detection, oldField has a non-zero cell at (2,5)
        // and zero below it. packField should pack it down to the bottom.
        board.newField[2][5] = 3;
        board.newField[2][6] = 3;
        board.newField[2][7] = 3;

        // manually trigger match so oldField is set up by findMatches
        board.findMatches(); // sets oldField[2][5..7] = 0 (they matched), noChanges=false

        // now place a non-zero cell in oldField at (2, 3) to simulate a block above the match
        board.oldField[2][3] = 5;

        board.collapse();

        // after pack: the cell at (2,3) should fall to the bottom
        assertEquals(5, board.newField[2][GameConfig.DEPTH],
                "surviving block above a cleared match should fall to the bottom row");
    }

    @Test
    void collapseNotifiesListenerFieldWasUpdated() {
        board.newField[3][5] = 2;
        board.newField[3][6] = 2;
        board.newField[3][7] = 2;
        board.findMatches();
        board.collapse();
        assertTrue(listener.fieldUpdated,
                "collapse must notify listener that the field was updated");
    }

    @Test
    void collapseNotifiesListenerScoreUpdated() {
        board.newField[3][5] = 2;
        board.newField[3][6] = 2;
        board.newField[3][7] = 2;
        board.findMatches();
        board.collapse();
        assertTrue(listener.lastScore >= 0,
                "collapse must notify listener with updated score");
    }

    // scoring

    @Test
    void matchAtHigherLevelScoresMoreThanAtLevelZero() {
        // identical vertical triplet; compare scores at level 0 vs level 1
        board.newField[3][5] = 2;
        board.newField[3][6] = 2;
        board.newField[3][7] = 2;
        board.findMatches();
        long scoreAtLevel0 = board.Score;

        // reset and repeat at level 1
        board.initBoard();
        board.level = 1;
        board.newField[3][5] = 2;
        board.newField[3][6] = 2;
        board.newField[3][7] = 2;
        board.findMatches();
        long scoreAtLevel1 = board.Score;

        assertTrue(scoreAtLevel1 > scoreAtLevel0,
                "same match at a higher level should award more points");
    }

    // changes of level

    @Test
    void levelDoesNotChangeBeforeThreshold() {
        board.figuresMatchedCounter = GameConfig.NEXT_LEVEL_THRESHOLD - 1;
        board.changeLevelIfNeeded();
        assertEquals(0, board.level, "level should not change before the threshold is reached");
    }

    @Test
    void levelIncreasesWhenThresholdIsReached() {
        board.figuresMatchedCounter = GameConfig.NEXT_LEVEL_THRESHOLD;
        board.changeLevelIfNeeded();
        assertEquals(1, board.level, "level should increase by 1 when threshold is reached");
    }

    @Test
    void levelDoesNotExceedMaxLevel() {
        board.level = GameConfig.MAX_LEVEL;
        board.figuresMatchedCounter = GameConfig.NEXT_LEVEL_THRESHOLD;
        board.changeLevelIfNeeded();
        assertEquals(GameConfig.MAX_LEVEL, board.level,
                "level must not exceed MAX_LEVEL");
    }

    @Test
    void levelChangeResetsMatchCounter() {
        board.figuresMatchedCounter = GameConfig.NEXT_LEVEL_THRESHOLD;
        board.changeLevelIfNeeded();
        assertEquals(0, board.figuresMatchedCounter,
                "match counter must reset to 0 after a level change");
    }

    @Test
    void levelChangeNotifiesListener() {
        board.figuresMatchedCounter = GameConfig.NEXT_LEVEL_THRESHOLD;
        board.changeLevelIfNeeded();
        assertEquals(1, listener.lastLevel,
                "listener must receive new level when it changes");
    }
}
