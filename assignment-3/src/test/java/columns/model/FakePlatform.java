package columns.model;

import columns.model.GameEvent;
import columns.model.kernel.Platform;
import columns.model.kernel.RandomGenerator;
import columns.model.kernel.Screen;

/**
 * Fake Platform for unit tests.
 * Time is controlled manually so no real sleeping occurs.
 * Events are queued so each getEvent() call pops the next one.
 * isKeyPressed() returns true as long as the event queue is non-empty.
 * Screen calls are recorded for assertion.
 */
class FakePlatform implements Platform {

    // timecontrol

    private long currentTime = 0;
    private long tc = 0;
    long delayCalledWith = -1;

    /** advance the fake clock by the given milliseconds. */
    void advanceTime(long millis) {
        currentTime += millis;
    }

    @Override public long currentTime() { return currentTime; }
    @Override public long getTc() { return tc; }
    @Override public void setTc(long time) { tc = time; }

    @Override public void delay(long t) { delayCalledWith = t; }

    //key and event control
    private final java.util.Queue<GameEvent> eventQueue = new java.util.ArrayDeque<>();
    private boolean keyPressed = false;

    // process event queue
    void queueEvent(GameEvent event) {
        eventQueue.add(event);
        keyPressed = true;
    }

    @Override
    public boolean isKeyPressed() {
        return !eventQueue.isEmpty();
    }

    @Override
    public void setKeyPressed(boolean isKeyPressed) {
        this.keyPressed = isKeyPressed;
        // consumed externally; queue drives the real flag
    }

    @Override
    public GameEvent getEvent() {
        return eventQueue.isEmpty() ? GameEvent.NONE : eventQueue.poll();
    }

    @Override public int getKeyPressed() { return 0; }

    //screen
    private final FakeScreen screen = new FakeScreen();
    @Override public Screen getScreen() { return screen; }
    FakeScreen getRecordingScreen() { return screen; }

    // random generator

    private RandomGenerator randomGenerator = () -> 0; // always returns color 1

    void setRandomGenerator(RandomGenerator rg) {
        this.randomGenerator = rg;
    }

    @Override public RandomGenerator getRandomGenerator() { return randomGenerator; }

    // fake-screen inner class

    static class FakeScreen implements Screen {
        final java.util.List<String> calls = new java.util.ArrayList<>();

        @Override public void setColor(int color) { calls.add("setColor:" + color); }
        @Override public void fillRect(int x, int y, int w, int h) { calls.add("fillRect:" + x + "," + y); }
        @Override public void drawRect(int x, int y, int w, int h) { calls.add("drawRect:" + x + "," + y); }
        @Override public void drawString(String s, int x, int y) { calls.add("drawString:" + s); }
        @Override public void clearRect(int x, int y, int w, int h) { calls.add("clearRect:" + x + "," + y); }
        @Override public int Black() { return 0; }
        @Override public int White() { return 8; }
    }
}
