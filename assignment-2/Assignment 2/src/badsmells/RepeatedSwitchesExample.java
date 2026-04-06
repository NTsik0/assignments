package badsmells;

/*
 Smell: Repeated Switches

 ORIGINAL PROBLEM:
 Two switch statements both branched on studentType. Adding a new type (e.g.
 "INTERNATIONAL") required coordinated edits in two separate methods. If a
 third or fourth method was added later the problem would multiply further.

 REFACTORINGS APPLIED:
 1. Replace Conditional with Polymorphism, StudentType becomes an enum that
    carries both pieces of data (tuitionDiscount, dormPriority) for each
    variant in one place.
 2. The two switch methods are replaced by simple data lookups on the enum
    constant, zero branching in the consuming code.

 WHY BETTER:
 Adding a new student type is a single enum constant addition; every consuming
 method automatically handles it. The duplication is eliminated. The Open/
 Closed Principle is respected: new variants extend the enum without modifying
 existing switch arms.
 */
public class RepeatedSwitchesExample {

    enum StudentType {
        STUDENT      (0.05, "NORMAL"),
        ATHLETE      (0.15, "HIGH"),
        EMPLOYEE_CHILD(0.25, "LOW");

        private final double tuitionDiscount;
        private final String dormPriority;

        StudentType(double tuitionDiscount, String dormPriority) {
            this.tuitionDiscount = tuitionDiscount;
            this.dormPriority    = dormPriority;
        }

        public double tuitionDiscount() { return tuitionDiscount; }
        public String dormPriority()    { return dormPriority;    }
    }

    public double tuitionDiscount(StudentType type) {
        return type.tuitionDiscount();
    }

    public String dormPriority(StudentType type) {
        return type.dormPriority();
    }

    public void clientCode() {
        System.out.println(tuitionDiscount(StudentType.ATHLETE));
        System.out.println(dormPriority(StudentType.ATHLETE));
    }
}
