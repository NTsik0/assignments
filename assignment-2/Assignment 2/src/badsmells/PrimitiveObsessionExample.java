package badsmells;

/*
 Smell: Primitive Obsession

 ORIGINAL PROBLEM:
 canRentDormRoom() accepted age, status, unpaidBalance, and countryCode as raw
 primitives. The "ACTIVE" and "GE" magic strings were scattered inline, and
 rules such as minimum age or maximum debt had no home — they were just
 anonymous numbers baked into one method.

 REFACTORINGS APPLIED:
 1. Replace Primitive with Enum, StudentStatus and CountryCode replace the
    raw strings and make invalid values impossible to construct.
 2. Extract Class, StudentAge and UnpaidBalance wrap the numeric primitives
    and own their own validation rules (isAdult(), isCleared()).
 3. Move Method, the eligibility check is expressed through the domain
    objects; canRentDormRoom() reads like a business rule statement.

 WHY BETTER:
 Magic strings are gone. Each domain concept owns its own rule. A change to
 the minimum age or the debt threshold touches only one class. The compiler
 rejects invalid statuses or country codes rather than letting them slip
 through as arbitrary strings.
 */
public class PrimitiveObsessionExample {

    enum StudentStatus  { ACTIVE, BLOCKED, GRADUATED }
    enum CountryCode    { GE, US, DE }

    static class StudentAge {
        private final int value;

        StudentAge(int value) { this.value = value; }

        public boolean isAdult() { return value >= 18; }
    }

    static class UnpaidBalance {
        private static final double MAXIMUM_ALLOWED = 100.0;
        private final double amount;

        UnpaidBalance(double amount) { this.amount = amount; }

        public boolean isCleared() { return amount < MAXIMUM_ALLOWED; }
    }

    public boolean canRentDormRoom(StudentAge age, StudentStatus status,
                                    UnpaidBalance balance, CountryCode country) {
        return age.isAdult()
            && status == StudentStatus.ACTIVE
            && balance.isCleared()
            && country == CountryCode.GE;
    }

    public void clientCode() {
        System.out.println(canRentDormRoom(
            new StudentAge(19), StudentStatus.ACTIVE,  new UnpaidBalance(0.0),   CountryCode.GE));
        System.out.println(canRentDormRoom(
            new StudentAge(17), StudentStatus.BLOCKED, new UnpaidBalance(120.0), CountryCode.US));
    }
}
