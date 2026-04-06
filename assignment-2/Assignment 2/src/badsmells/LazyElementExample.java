package badsmells;

/*
 Smell: Lazy Element

 ORIGINAL PROBLEM:
 StudentNameFormatter existed only to wrap String.trim(). The class added no
 real variation, no policy, and no reusable abstraction, just noise.

 REFACTORINGS APPLIED:
 1. Inline Class, the formatter is removed; the call site calls trim() directly.

 WHY BETTER:
 One less class to navigate. The intent is immediately clear at the call site.
 The abstraction is removed because it bought nothing.
 */
public class LazyElementExample {

    public void clientCode() {
        System.out.println("  Nino  ".trim());
    }
}
