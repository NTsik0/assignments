package columns.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Figure movement and rotation behavior.
 * Uses a deterministic RandomGenerator that returns a fixed sequence so
 * figure colors are always predictable (no uncontrolled randomness).
 */
class FigureTest {

    // Returns 1, 2, 3, 1, 2, 3, ... → colors c[1]=2, c[2]=3, c[3]=1
    // (Math.abs(n) % 7 + 1, so values 1,2,3 → colors 2,3,1)
    private static columns.model.kernel.RandomGenerator seqGen(int... values) {
        return new columns.model.kernel.RandomGenerator() {
            int idx = 0;
            @Override
            public int nextInt() {
                return values[idx++ % values.length];
            }
        };
    }

    private Figure makeFigure(int... colors) {
        // colors[0] → c[1], colors[1] → c[2], colors[2] → c[3]
        // randomGenerator returns v → color = Math.abs(v) % 7 + 1
        // To get color k, supply v = k - 1
        return new Figure(seqGen(colors[0] - 1, colors[1] - 1, colors[2] - 1));
    }

    // contruction of figure

    @Test
    void figureStartsAtHorizontalCenter() {
        Figure f = makeFigure(1, 2, 3);
        assertEquals(GameConfig.WIDTH / 2 + 1, f.x,
                "new figure x should be at horizontal center column");
    }

    @Test
    void figureStartsAtTopRow() {
        Figure f = makeFigure(1, 2, 3);
        assertEquals(1, f.y, "new figure y should start at row 1");
    }

    @Test
    void figureColorsAreSetFromRandomGenerator() {
        Figure f = makeFigure(2, 5, 7);
        assertAll(
                () -> assertEquals(2, f.c[1], "c[1] should match first random value"),
                () -> assertEquals(5, f.c[2], "c[2] should match second random value"),
                () -> assertEquals(7, f.c[3], "c[3] should match third random value")
        );
    }

    @Test
    void figureColorValuesAreInValidRange() {
        //  formula is Math.abs(random.nextInt()) % 7 + 1, so [1..7]
        Figure f = new Figure(seqGen(0, 6, 13));
        assertAll(
                () -> assertTrue(f.c[1] >= 1 && f.c[1] <= 7),
                () -> assertTrue(f.c[2] >= 1 && f.c[2] <= 7),
                () -> assertTrue(f.c[3] >= 1 && f.c[3] <= 7)
        );
    }

    // horizontal movement/ moveRight MoveLeft

    @Test
    void moveRightIncrementsX() {
        Figure f = makeFigure(1, 2, 3);
        int before = f.x;
        f.moveRight();
        assertEquals(before + 1, f.x, "moveRight should increment x by 1");
    }

    @Test
    void moveLeftDecrementsX() {
        Figure f = makeFigure(1, 2, 3);
        int before = f.x;
        f.moveLeft();
        assertEquals(before - 1, f.x, "moveLeft should decrement x by 1");
    }

    @Test
    void moveRightAndLeftAreInverse() {
        Figure f = makeFigure(1, 2, 3);
        int before = f.x;
        f.moveRight();
        f.moveLeft();
        assertEquals(before, f.x, "moveRight then moveLeft should return to original x");
    }

    // vertical movement down and up

    @Test
    void moveDownIncrementsY() {
        Figure f = makeFigure(1, 2, 3);
        int before = f.y;
        f.moveDown();
        assertEquals(before + 1, f.y, "moveDown should increment y by 1");
    }

    // rotation

    @Test
    void rotateUpShiftsColorsUpward() {
        Figure f = makeFigure(1, 2, 3);
        // before: c[1]=1, c[2]=2, c[3]=3
        f.rotateUp();
        // after:  c[1]=2, c[2]=3, c[3]=1
        assertAll(
                () -> assertEquals(2, f.c[1], "rotateUp: c[1] should become old c[2]"),
                () -> assertEquals(3, f.c[2], "rotateUp: c[2] should become old c[3]"),
                () -> assertEquals(1, f.c[3], "rotateUp: c[3] should become old c[1]")
        );
    }

    @Test
    void rotateDownShiftsColorsDownward() {
        Figure f = makeFigure(1, 2, 3);
        // before: c[1]=1, c[2]=2, c[3]=3
        f.rotateDown();
        // after:  c[1]=3, c[2]=1, c[3]=2
        assertAll(
                () -> assertEquals(3, f.c[1], "rotateDown: c[1] should become old c[3]"),
                () -> assertEquals(1, f.c[2], "rotateDown: c[2] should become old c[1]"),
                () -> assertEquals(2, f.c[3], "rotateDown: c[3] should become old c[2]")
        );
    }

    @Test
    void rotateUpThenDownRestoresOriginalColors() {
        Figure f = makeFigure(2, 5, 7);
        int c1 = f.c[1], c2 = f.c[2], c3 = f.c[3];
        f.rotateUp();
        f.rotateDown();
        assertAll(
                () -> assertEquals(c1, f.c[1], "c[1] should be restored"),
                () -> assertEquals(c2, f.c[2], "c[2] should be restored"),
                () -> assertEquals(c3, f.c[3], "c[3] should be restored")
        );
    }

    @Test
    void threeRotateUpsRestoreOriginalColors() {
        Figure f = makeFigure(2, 5, 7);
        int c1 = f.c[1], c2 = f.c[2], c3 = f.c[3];
        f.rotateUp();
        f.rotateUp();
        f.rotateUp();
        assertAll(
                () -> assertEquals(c1, f.c[1], "3x rotateUp: c[1] should be original"),
                () -> assertEquals(c2, f.c[2], "3x rotateUp: c[2] should be original"),
                () -> assertEquals(c3, f.c[3], "3x rotateUp: c[3] should be original")
        );
    }

    @Test
    void threeRotateDownsRestoreOriginalColors() {
        Figure f = makeFigure(3, 6, 1);
        int c1 = f.c[1], c2 = f.c[2], c3 = f.c[3];
        f.rotateDown();
        f.rotateDown();
        f.rotateDown();
        assertAll(
                () -> assertEquals(c1, f.c[1], "3x rotateDown: c[1] should be original"),
                () -> assertEquals(c2, f.c[2], "3x rotateDown: c[2] should be original"),
                () -> assertEquals(c3, f.c[3], "3x rotateDown: c[3] should be original")
        );
    }

    @Test
    void rotationDoesNotChangePosition() {
        Figure f = makeFigure(1, 2, 3);
        int xBefore = f.x;
        int yBefore = f.y;
        f.rotateUp();
        f.rotateDown();
        assertAll(
                () -> assertEquals(xBefore, f.x, "rotation must not change x"),
                () -> assertEquals(yBefore, f.y, "rotation must not change y")
        );
    }
}
