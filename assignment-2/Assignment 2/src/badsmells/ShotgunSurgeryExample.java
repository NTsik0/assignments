package badsmells;

/*
 Smell: Shotgun Surgery

 ORIGINAL PROBLEM:
 Course title presentation was duplicated across Course.label(),
 Invoice.description(), and Certificate.text(). Renaming the prefix word
 "Course:" would require three separate edits. Forgetting one would create
 inconsistency.

 REFACTORINGS APPLIED:
 1. Extract Class, CourseTitleFormatter centralises all formatting rules for
    a course title: label, invoice description, and certificate text.
 2. Move Method, each formatting method moves into the formatter.
 3. Course, Invoice, and Certificate delegate to the formatter, so they never
    embed their own presentation logic.

 WHY BETTER:
 There is now a single place for course-title formatting. Any wording change
 is made once in CourseTitleFormatter and propagates everywhere. The three
 domain classes remain focused on their own responsibilities.
 */
public class ShotgunSurgeryExample {

    static class CourseTitleFormatter {
        private final String title;

        CourseTitleFormatter(String title) {
            this.title = title;
        }

        public String label()       { return "Course: "     + title; }
        public String invoiceDescription() { return "Invoice for " + title; }
        public String certificateText()    { return "Completed "   + title; }
    }

    static class Course {
        private final CourseTitleFormatter formatter;

        Course(String title) {
            this.formatter = new CourseTitleFormatter(title);
        }

        public String label() { return formatter.label(); }
    }

    static class Invoice {
        private final CourseTitleFormatter formatter;

        Invoice(String courseTitle) {
            this.formatter = new CourseTitleFormatter(courseTitle);
        }

        public String description() { return formatter.invoiceDescription(); }
    }

    static class Certificate {
        private final CourseTitleFormatter formatter;

        Certificate(String courseTitle) {
            this.formatter = new CourseTitleFormatter(courseTitle);
        }

        public String text() { return formatter.certificateText(); }
    }

    public void clientCode() {
        Course      course      = new Course("Refactoring");
        Invoice     invoice     = new Invoice("Refactoring");
        Certificate certificate = new Certificate("Refactoring");

        System.out.println(course.label());
        System.out.println(invoice.description());
        System.out.println(certificate.text());
    }
}
