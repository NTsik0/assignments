package badsmells;

import java.util.List;
import java.util.stream.Collectors;

/*
 Smell: Loops

 ORIGINAL PROBLEM:
 The for-loop in honorStudents() hid a simple two-step data transformation
 (filter by GPA, collect names) behind manual control-flow machinery. The
 reader had to trace the loop variable and accumulator instead of reading the
 intent directly.

 REFACTORINGS APPLIED:
 1. Replace Loop with Pipeline, stream().filter().map().collect() expresses
    the intent in a single readable expression.
 2. Extract Method, isHonorStudent() names the selection predicate, making
    it reusable and independently testable.

 WHY BETTER:
  The pipeline reads like a declarative specification: keep honor students,
  return their names. The control-flow mechanics are gone. The predicate can
  be reused or changed in one place.
 */
public class LoopsExample {

    static class Student {
        final String name;
        final double gpa;

        Student(String name, double gpa) {
            this.name = name;
            this.gpa  = gpa;
        }
    }

    public List<String> honorStudents(List<Student> students) {
        return students.stream()
            .filter(this::isHonorStudent)
            .map(s -> s.name)
            .collect(Collectors.toList());
    }

    private boolean isHonorStudent(Student student) {
        return student.gpa > 3.5;
    }

    public void clientCode() {
        List<Student> students = List.of(
            new Student("Nino",   3.9),
            new Student("Giorgi", 3.1),
            new Student("Maka",   3.7)
        );
        System.out.println(honorStudents(students));
    }
}
