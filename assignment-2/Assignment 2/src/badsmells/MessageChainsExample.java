package badsmells;

/*
 Smell: Message Chains

 ORIGINAL PROBLEM:
 The client navigated four objects deep:
   university.getDepartment().getCoordinator().getOffice().getPhoneNumber()
 This coupled the caller to the internal object graph. Any restructuring of
 Department, Coordinator, or Office would ripple to every call site.

 REFACTORINGS APPLIED:
 1. Hide Delegate (Extract Method), University exposes
    getCoordinatorPhoneNumber() which internally traverses the chain.
 2. The intermediate getters (getDepartment, getCoordinator, getOffice) are
    made package-private so they are no longer part of the public API.

 WHY BETTER:
 The client asks one object for one piece of information. The internal
 structure can change without touching call sites. The coupling point shrinks
 from four objects to one.
 */
public class MessageChainsExample {

    static class University {
        public String getCoordinatorPhoneNumber() {
            return getDepartment().getCoordinator().getOffice().getPhoneNumber();
        }

        Department getDepartment()       { return new Department(); }
    }

    static class Department {
        Coordinator getCoordinator()     { return new Coordinator(); }
    }

    static class Coordinator {
        Office getOffice()               { return new Office(); }
    }

    static class Office {
        String getPhoneNumber()          { return "555-0101"; }
    }

    public void clientCode() {
        University university = new University();
        System.out.println(university.getCoordinatorPhoneNumber());
    }
}
