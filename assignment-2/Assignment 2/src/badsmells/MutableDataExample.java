package badsmells;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Smell: Mutable Data

 ORIGINAL PROBLEM:
 getEnrolledStudents() returned the real internal list. Any caller could call
 clear(), remove(), or add() on it, mutating the object's state without going
 through any controlled method. clientCode() demonstrated exactly this problem.

 REFACTORINGS APPLIED:
 1. Encapsulate Field, the list is private and final.
 2. Return Unmodifiable View, getEnrolledStudents() wraps the list with
 Collections.unmodifiableList() so callers can read but not mutate.
 3. Encapsulate Updates, enroll() is the only way to add students.

 WHY BETTER:
 External code can no longer corrupt the enrollment list. All mutations go
 through enroll(), which is the natural place to add validation or events
 in the future. The client code in the original broke silently; now it would
 throw UnsupportedOperationException, making the contract explicit.
 */
public class MutableDataExample {

    private final List<String> enrolledStudents = new ArrayList<>();

    public List<String> getEnrolledStudents() {
        return Collections.unmodifiableList(enrolledStudents);
    }

    public void enroll(String studentId) {
        enrolledStudents.add(studentId);
    }

    public void clientCode() {
        enroll("s-1001");
        List<String> students = getEnrolledStudents();
        // students.clear() would now throw UnsupportedOperationException,
        // making the protection explicit rather than silent.
        System.out.println(getEnrolledStudents().size()); // prints 1
    }
}
