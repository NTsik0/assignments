# Assignment 2 - Refactoring

**Nikoloz Tsikaridze**

This assignment covers 24 code smells and the refactorings applied to fix each one. Every smell has its own Java file with the original problematic code and the cleaned up version.

## Code Smells Covered

| # | File | Smell | Main Refactoring |
|---|------|-------|-----------------|
| 1 | AlternativeClassesWithDifferentInterfacesExample.java | Two classes doing the same job with different method names | Extract Interface |
| 2 | CommentsExample.java | Comments compensating for unclear code | Extract Method, Rename |
| 3 | DataClassExample.java | Passive class with no behaviour | Move Method, Encapsulate Fields |
| 4 | DataClumpsExample.java | Same three parameters repeated everywhere | Introduce Parameter Object |
| 5 | DivergentChangeExample.java | One class with three unrelated reasons to change | Extract Class |
| 6 | DuplicatedCodeExample.java | Identical tax and shipping logic in two methods | Extract Method |
| 7 | FeatureEnvyExample.java | Method more interested in another class's data | Move Method |
| 8 | GlobalDataExample.java | Public static fields changeable from anywhere | Encapsulate Fields, Extract Class |
| 9 | InsiderTradingExample.java | Direct field access between unrelated classes | Encapsulate Fields, Move Method |
| 10 | LargeClassExample.java | Eight unrelated concerns in one class | Extract Class |
| 11 | LazyElementExample.java | Class that only calls trim() | Inline Class |
| 12 | LongFunctionExample.java | 40-line method mixing five concerns | Extract Method |
| 13 | LongParameterListExample.java | Method with 12 parameters | Introduce Parameter Object |
| 14 | LoopsExample.java | Manual loop hiding a simple filter and map | Replace Loop with Pipeline |
| 15 | MessageChainsExample.java | Client navigating four objects deep | Hide Delegate |
| 16 | MiddleManExample.java | Class that only forwards calls | Remove Middle Man |
| 17 | MutableDataExample.java | Returning live internal list to callers | Return Unmodifiable View |
| 18 | MysteriousNameExample.java | Method named f() with parameters a, b, c | Rename Method, Rename Variables |
| 19 | PrimitiveObsessionExample.java | Magic strings and scattered thresholds | Replace Primitive with Enum, Extract Class |
| 20 | RefusedBequestExample.java | Subclass throwing UnsupportedOperationException | Extract Superclass, Push Down Method |
| 21 | RepeatedSwitchesExample.java | Same switch statement in two places | Replace Conditional with Polymorphism |
| 22 | ShotgunSurgeryExample.java | Title formatting logic duplicated in three classes | Extract Class, Move Method |
| 23 | SpeculativeGeneralityExample.java | Parameters added for imaginary future use | Change Method Signature |
| 24 | TemporaryFieldExample.java | Fields that are only valid half the time | Extract Class |

---

## Key Principles Applied

- **Single Responsibility** - each class has one reason to change
- **Encapsulation** - data and the behaviour that uses it live together
- **Open/Closed** - new variants added without modifying existing code
- **Liskov Substitution** - subclasses honour the contracts of their parents
- **YAGNI** - speculative code removed, not added
