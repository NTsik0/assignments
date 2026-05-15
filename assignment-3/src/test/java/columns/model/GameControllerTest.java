package columns.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GameController event handling.
 * Strategy: create a GameController with a FakePlatform so no real screen,
 * sleep, or random behavior is needed. We then plant a known Figure on the
 * board and call processEvent() directly — this is the package-private method
 * that handles all user events. This avoids exercising the full game loop
 * (which blocks) while still verifying every event's behavioral outcome.
 */
class GameControllerTest {

    private FakePlatform platform;
    private GameController controller;

    // Deterministic random: always gives remainder 0 → color = 1
    private static columns.model.kernel.RandomGenerator fixedRandom(int... values) {
        return new columns.model.kernel.RandomGenerator() {
            int idx = 0;
            @Override public int nextInt() { return values[idx++ % values.length]; }
        };
    }

    /** make a figure with explicit colors. */
    private static Figure fig(int c1, int c2, int c3) {
        return new Figure(fixedRandom(c1 - 1, c2 - 1, c3 - 1));
    }

    @BeforeEach
    void setUp() {
        platform = new FakePlatform();
        controller = new GameController(platform);
        // initialise the board manually (same as runGameLoop start)
        controller.board.initBoard();
        // Plant a known figure in the middle of  board
        Figure f = fig(1, 2, 3);
        f.x = 4; // mid-column
        f.y = 5; // mid-row
        controller.board.figure = f;
    }

    // left event

    @Test
    void leftEventMovesXLeftByOne() {
        int xBefore = controller.board.figure.x;
        controller.processEvent(GameEvent.LEFT);
        assertEquals(xBefore - 1, controller.board.figure.x,
                "LEFT event should decrement figure x by 1");
    }

    @Test
    void leftEventDoesNotMoveFigureThroughLeftWall() {
        controller.board.figure.x = 1;
        controller.processEvent(GameEvent.LEFT);
        assertEquals(1, controller.board.figure.x,
                "LEFT event at left border must not move the figure further left");
    }

    @Test
    void leftEventDoesNotMoveFigureIntoOccupiedCell() {
        controller.board.figure.x = 4;
        controller.board.figure.y = 5;
        // block at (x-1, y+2) = (3, 7)
        controller.board.newField[3][7] = 2;
        int xBefore = controller.board.figure.x;
        controller.processEvent(GameEvent.LEFT);
        assertEquals(xBefore, controller.board.figure.x,
                "LEFT event must not move figure into an occupied cell");
    }

    // right event

    @Test
    void rightEventMovesXRightByOne() {
        int xBefore = controller.board.figure.x;
        controller.processEvent(GameEvent.RIGHT);
        assertEquals(xBefore + 1, controller.board.figure.x,
                "RIGHT event should increment figure x by 1");
    }

    @Test
    void rightEventDoesNotMoveFigureThroughRightWall() {
        controller.board.figure.x = GameConfig.WIDTH;
        controller.processEvent(GameEvent.RIGHT);
        assertEquals(GameConfig.WIDTH, controller.board.figure.x,
                "RIGHT event at right border must not move the figure further right");
    }

    @Test
    void rightEventDoesNotMoveFigureIntoOccupiedCell() {
        controller.board.figure.x = 4;
        controller.board.figure.y = 5;
        // Block at (x+1, y+2) = (5, 7)
        controller.board.newField[5][7] = 2;
        int xBefore = controller.board.figure.x;
        controller.processEvent(GameEvent.RIGHT);
        assertEquals(xBefore, controller.board.figure.x,
                "RIGHT event must not move figure into an occupied cell");
    }

    // up rotate up event

    @Test
    void upEventRotatesFigureColorsUpward() {
        int c1 = controller.board.figure.c[1];
        int c2 = controller.board.figure.c[2];
        int c3 = controller.board.figure.c[3];
        controller.processEvent(GameEvent.UP);
        assertAll(
                () -> assertEquals(c2, controller.board.figure.c[1], "UP: c[1] becomes old c[2]"),
                () -> assertEquals(c3, controller.board.figure.c[2], "UP: c[2] becomes old c[3]"),
                () -> assertEquals(c1, controller.board.figure.c[3], "UP: c[3] becomes old c[1]")
        );
    }

    @Test
    void upEventDoesNotChangePosition() {
        int x = controller.board.figure.x;
        int y = controller.board.figure.y;
        controller.processEvent(GameEvent.UP);
        assertAll(
                () -> assertEquals(x, controller.board.figure.x, "UP must not change x"),
                () -> assertEquals(y, controller.board.figure.y, "UP must not change y")
        );
    }

    // down rotate down event
    @Test
    void downEventRotatesFigureColorsDownward() {
        int c1 = controller.board.figure.c[1];
        int c2 = controller.board.figure.c[2];
        int c3 = controller.board.figure.c[3];
        controller.processEvent(GameEvent.DOWN);
        assertAll(
                () -> assertEquals(c3, controller.board.figure.c[1], "DOWN: c[1] becomes old c[3]"),
                () -> assertEquals(c1, controller.board.figure.c[2], "DOWN: c[2] becomes old c[1]"),
                () -> assertEquals(c2, controller.board.figure.c[3], "DOWN: c[3] becomes old c[2]")
        );
    }

    @Test
    void downEventDoesNotChangePosition() {
        int x = controller.board.figure.x;
        int y = controller.board.figure.y;
        controller.processEvent(GameEvent.DOWN);
        assertAll(
                () -> assertEquals(x, controller.board.figure.x, "DOWN must not change x"),
                () -> assertEquals(y, controller.board.figure.y, "DOWN must not change y")
        );
    }

    // drop event

    @Test
    void dropEventMovesYToNearBottom() {
        controller.board.figure.x = 3;
        controller.board.figure.y = 2;
        controller.processEvent(GameEvent.DROP);
        // On an empty board the figure drops to DEPTH - 2
        assertEquals(GameConfig.DEPTH - 2, controller.board.figure.y,
                "DROP event should move figure to bottom of empty column");
    }

    @Test
    void dropEventSetsTcToZeroToForceImmediateStep() {
        // after drop the platform.tc should be 0 so the game loop
        // immediately recognises that enough time has elapsed to paste.
        controller.processEvent(GameEvent.DROP);
        assertEquals(0, platform.getTc(),
                "DROP event must reset tc to 0 so the fall is committed immediately");
    }

    @Test
    void dropEventStopsAboveExistingBlock() {
        controller.board.figure.x = 3;
        controller.board.figure.y = 2;
        // dropFigure starts zz at DEPTH=15 and decrements while occupied.
        // block at row 15 (the floor): zz=15 occupied → zz=14 empty → stop.
        // f.y = 14 - 2 = 12.
        controller.board.newField[3][15] = 5;
        controller.processEvent(GameEvent.DROP);
        assertEquals(12, controller.board.figure.y,
                "DROP event should land figure on top of block at the bottom row");
    }

    // level-up event

    @Test
    void levelUpEventIncreasesLevel() {
        controller.board.level = 2;
        controller.processEvent(GameEvent.LEVEL_UP);
        assertEquals(3, controller.board.level,
                "LEVEL_UP event should increment level by 1");
    }

    @Test
    void levelUpEventDoesNotExceedMaxLevel() {
        controller.board.level = GameConfig.MAX_LEVEL;
        controller.processEvent(GameEvent.LEVEL_UP);
        assertEquals(GameConfig.MAX_LEVEL, controller.board.level,
                "LEVEL_UP at max level must not exceed MAX_LEVEL");
    }

    @Test
    void levelUpEventResetsMatchCounter() {
        controller.board.level = 2;
        controller.board.figuresMatchedCounter = 15;
        controller.processEvent(GameEvent.LEVEL_UP);
        assertEquals(0, controller.board.figuresMatchedCounter,
                "LEVEL_UP must reset the match counter");
    }

    // level-down event

    @Test
    void levelDownEventDecreasesLevel() {
        controller.board.level = 3;
        controller.processEvent(GameEvent.LEVEL_DOWN);
        assertEquals(2, controller.board.level,
                "LEVEL_DOWN event should decrement level by 1");
    }

    @Test
    void levelDownEventDoesNotGoBelowZero() {
        controller.board.level = 0;
        controller.processEvent(GameEvent.LEVEL_DOWN);
        assertEquals(0, controller.board.level,
                "LEVEL_DOWN at level 0 must not go below 0");
    }

    @Test
    void levelDownEventResetsMatchCounter() {
        controller.board.level = 2;
        controller.board.figuresMatchedCounter = 20;
        controller.processEvent(GameEvent.LEVEL_DOWN);
        assertEquals(0, controller.board.figuresMatchedCounter,
                "LEVEL_DOWN must reset the match counter");
    }

    // none event / change nothing on board

    @Test
    void noneEventChangesNothingOnBoard() {
        int x = controller.board.figure.x;
        int y = controller.board.figure.y;
        int level = controller.board.level;
        long score = controller.board.Score;
        controller.processEvent(GameEvent.NONE);
        assertAll(
                () -> assertEquals(x, controller.board.figure.x),
                () -> assertEquals(y, controller.board.figure.y),
                () -> assertEquals(level, controller.board.level),
                () -> assertEquals(score, controller.board.Score)
        );
    }

    // here is symmetry between up and down events

    @Test
    void upThenDownRestoresOriginalColors() {
        int c1 = controller.board.figure.c[1];
        int c2 = controller.board.figure.c[2];
        int c3 = controller.board.figure.c[3];
        controller.processEvent(GameEvent.UP);
        controller.processEvent(GameEvent.DOWN);
        assertAll(
                () -> assertEquals(c1, controller.board.figure.c[1], "UP then DOWN: c[1] restored"),
                () -> assertEquals(c2, controller.board.figure.c[2], "UP then DOWN: c[2] restored"),
                () -> assertEquals(c3, controller.board.figure.c[3], "UP then DOWN: c[3] restored")
        );
    }
}
