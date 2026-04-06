package badsmells;

/*
 Smell: Global Data

 ORIGINAL PROBLEM:
 currentSemester and tuitionRate were public static fields reachable from
 anywhere. Any class could mutate them without the owning class knowing,
 making bugs nearly impossible to trace.

 REFACTORINGS APPLIED:
 1. Extract Class, AcademicRegistry encapsulates the shared state.
 2. Encapsulate Fields, fields are private; changes go through controlled
    methods (openFallSemester, approveRateIncrease, applyEmergencyIncrease).
 3. Move Method, behaviour that changes the state moves into the new class.
 4. BillingService and SemesterAdministration receive the registry via
    constructor (dependency injection) instead of reaching into statics.

 WHY BETTER:
 All mutations to semester and rate data pass through one class. Debugging a
 wrong rate means looking only at AcademicRegistry. Client classes are
 decoupled from each other and depend on an interface, not global state.
 */
public class GlobalDataExample {

    static class AcademicRegistry {
        private String currentSemester = "SPRING";
        private double tuitionRate     = 1250.0;

        public String getCurrentSemester() { return currentSemester; }
        public double getTuitionRate()     { return tuitionRate; }

        public void openFallSemester()              { currentSemester = "FALL"; }
        public void approveRateIncrease()           { tuitionRate += 100; }
        public void applyEmergencyIncrease(double delta) { tuitionRate += delta; }
    }

    static class BillingService {
        private final AcademicRegistry registry;

        BillingService(AcademicRegistry registry) {
            this.registry = registry;
        }

        public double calculateInvoice(int credits) {
            return credits * registry.getTuitionRate();
        }
    }

    static class SemesterAdministration {
        private final AcademicRegistry registry;

        SemesterAdministration(AcademicRegistry registry) {
            this.registry = registry;
        }

        public void openFallSemester()  { registry.openFallSemester(); }
        public void approveRateIncrease() { registry.approveRateIncrease(); }
    }

    public void clientCode() {
        AcademicRegistry    registry       = new AcademicRegistry();
        BillingService      billingService = new BillingService(registry);
        SemesterAdministration administration = new SemesterAdministration(registry);

        System.out.println(registry.getCurrentSemester());
        System.out.println(billingService.calculateInvoice(3));
        administration.openFallSemester();
        administration.approveRateIncrease();
        System.out.println(registry.getCurrentSemester());
        System.out.println(billingService.calculateInvoice(3));
    }
}
