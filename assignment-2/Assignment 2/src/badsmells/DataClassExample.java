package badsmells;

/*
 Smell: Data Class

 ORIGINAL PROBLEM:
 StudentRecord was a passive bag of public fields. All logic that operated on
 student data (honors eligibility, tuition discount, academic standing) lived
 in three separate helper classes, leaving the domain model weak and anemic.

 REFACTORINGS APPLIED:
 1. Encapsulate Fields, fields are now private with a constructor.
 2. Move Method, isEligibleForHonors(), tuitionDiscountPercent(), and
    academicStanding() are moved into StudentRecord where the data lives.
 3. The three helper classes are removed (their only job was operating on the
    record's data, so inlining them into the record is correct here).

 WHY BETTER:
 StudentRecord now owns the behavior that depends on its own state. The client
 asks the student object directly, which is cleaner, more cohesive, and easier
 to test. No external class can corrupt the fields because they are private.
 */
public class DataClassExample {

    public static class StudentRecord {
        private final String name;
        private final int credits;
        private final double gpa;

        public StudentRecord(String name, int credits, double gpa) {
            this.name    = name;
            this.credits = credits;
            this.gpa     = gpa;
        }

        public String getName() { return name; }

        public boolean isEligibleForHonors() {
            return credits >= 30 && gpa >= 3.7;
        }

        public double tuitionDiscountPercent() {
            if (gpa >= 3.8) return 0.15;
            if (gpa >= 3.5) return 0.10;
            return 0.0;
        }

        public String academicStanding() {
            if (gpa < 2.0)     return name + " is on academic probation";
            if (credits < 15)  return name + " is a new student";
            return name + " is in good standing";
        }
    }

    public void clientCode() {
        StudentRecord student = new StudentRecord("Nino", 32, 3.8);
        System.out.println(student.isEligibleForHonors());
        System.out.println(student.tuitionDiscountPercent());
        System.out.println(student.academicStanding());
    }
}
