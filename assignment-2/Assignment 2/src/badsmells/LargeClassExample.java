package badsmells;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 Smell: Large Class

 ORIGINAL PROBLEM:
 One class held enrollment, staffing, finance, cafeteria, transport, help desk,
 payroll, and website state and behaviour. Eight unrelated domain areas were
 crammed into a single abstraction, making the class hard to understand,
 test, and change without accidental side effects.

 REFACTORINGS APPLIED:
 1. Extract Class for each cohesive responsibility:
    - AcademicRegistry  (students, teachers, courses)
    - FinanceOffice      (budget, tuition, salaries)
    - CampusServices     (cafeteria, bus schedule, help desk)
    - HrOffice           (payroll day)
    - WebsiteAdmin       (website theme)
 2. Move Method and field, each field and method follows the concern it belongs to.
 3. LargeClassExample becomes a thin coordinator that holds the extracted objects.

 WHY BETTER:
 Each extracted class has a single reason to change. A new payroll feature
 touches only HrOffice; a cafeteria redesign touches only CampusServices.
 The classes are individually small enough to read and test in isolation.
 */
public class LargeClassExample {

    static class AcademicRegistry {
        private final List<String> students = new ArrayList<>();
        private final List<String> teachers = new ArrayList<>();
        private final List<String> courses  = new ArrayList<>();

        public void enrollStudent(String student)  { students.add(student); }
        public void hireTeacher(String teacher)    { teachers.add(teacher); }
        public void addCourse(String course)       { courses.add(course);   }

        public List<String> getStudents() { return Collections.unmodifiableList(students); }
        public List<String> getTeachers() { return Collections.unmodifiableList(teachers); }
        public List<String> getCourses()  { return Collections.unmodifiableList(courses);  }
    }

    static class FinanceOffice {
        private double budget;

        public void chargeTuition(double amount) { budget += amount; }
        public void paySalary(double amount)     { budget -= amount; }
        public double getBudget()                { return budget;    }
    }

    static class CampusServices {
        private int    openTickets;
        private String cafeteriaMenu;
        private String busSchedule;

        public void openHelpDeskTicket()              { openTickets++;          }
        public void publishCafeteriaMenu(String menu) { cafeteriaMenu = menu;   }
        public void publishBusSchedule(String sched)  { busSchedule  = sched;   }

        public int    getOpenTickets()    { return openTickets;    }
        public String getCafeteriaMenu()  { return cafeteriaMenu;  }
        public String getBusSchedule()    { return busSchedule;    }
    }

    static class HrOffice {
        private String payrollDay;

        public void setPayrollDay(String day) { payrollDay = day; }
        public String getPayrollDay()         { return payrollDay; }
    }

    static class WebsiteAdmin {
        private String theme;

        public void updateTheme(String newTheme) { theme = newTheme; }
        public String getTheme()                 { return theme;     }
    }

    // ---- thin coordinator ----
    private final AcademicRegistry academicRegistry = new AcademicRegistry();
    private final FinanceOffice    financeOffice    = new FinanceOffice();
    private final CampusServices   campusServices   = new CampusServices();
    private final HrOffice         hrOffice         = new HrOffice();
    private final WebsiteAdmin     websiteAdmin     = new WebsiteAdmin();

    public void clientCode() {
        academicRegistry.enrollStudent("Nino");
        academicRegistry.hireTeacher("Ms. Kapanadze");
        academicRegistry.addCourse("Refactoring");

        financeOffice.chargeTuition(2400);
        financeOffice.paySalary(1200);

        campusServices.openHelpDeskTicket();
        campusServices.publishBusSchedule("Route A at 08:00");
        campusServices.publishCafeteriaMenu("Soup and salad");

        hrOffice.setPayrollDay("Friday");
        websiteAdmin.updateTheme("blue");
    }
}
