package badsmells;

/*
 Smell: Refused Bequest

 ORIGINAL PROBLEM:
 Penguin extended Bird and was forced to override fly() with an
 UnsupportedOperationException. Any code that held a Bird reference and called
 fly() could blow up at runtime, a direct violation of the Liskov
 Substitution Principle. The inheritance relationship was a lie.

 REFACTORINGS APPLIED:
 1. Extract Superclass, Bird becomes a base with only the behaviour all birds
    share (here: move(), a template for locomotion).
 2. Push Down, fly() is pushed down into a new FlyingBird subclass.
 3. Penguin now extends Bird directly and is never forced to implement fly().
 4. Use Supertype Where Possible, client code works through Bird for common
    operations and through FlyingBird only when flight is expected.

 WHY BETTER:
 No bird throws at runtime. Penguin is a legitimate Bird. FlyingBird is a
 legitimate subtype that genuinely supports flying. The hierarchy now matches
 the real-world "is-a" relationship and the LSP is satisfied.
 */
public class RefusedBequestExample {

    static abstract class Bird {
        public abstract void move();
    }

    static abstract class FlyingBird extends Bird {
        public abstract void fly();

        @Override
        public void move() { fly(); }
    }

    static class Sparrow extends FlyingBird {
        @Override
        public void fly() { System.out.println("Sparrow is flying"); }
    }

    static class Eagle extends FlyingBird {
        @Override
        public void fly() { System.out.println("Eagle is soaring"); }
    }

    static class Penguin extends Bird {
        @Override
        public void move() { System.out.println("Penguin is waddling"); }
    }

    public void clientCode() {
        Bird sparrow = new Sparrow();
        Bird eagle   = new Eagle();
        Bird penguin = new Penguin();

        sparrow.move();   // Sparrow is flying
        eagle.move();     // Eagle is soaring
        penguin.move();   // Penguin is waddling  — no exception, no lie

        // When flight is specifically needed, use the narrower type:
        FlyingBird soarer = new Eagle();
        soarer.fly();
    }
}
