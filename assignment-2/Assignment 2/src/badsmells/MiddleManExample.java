package badsmells;

/*
 Smell: Middle Man

 ORIGINAL PROBLEM:
 StudentPortal.findGrade() did nothing but forward the call to
 TranscriptService.findGrade(). The extra class added indirection without
 adding any encapsulation benefit, policy, or translation.

 REFACTORINGS APPLIED:
 1. Remove Middle Man (Inline Class), StudentPortal is removed.
    Client code talks to TranscriptService directly.

 WHY BETTER:
 One fewer class and one fewer indirection. The call chain is honest about
 where the behaviour lives. If StudentPortal later grows real responsibilities
 (e.g. access control, caching), it can be reintroduced at that point.
 */
public class MiddleManExample {

    static class TranscriptService {
        public String findGrade(String studentId) {
            return "A";
        }
    }

    public void clientCode() {
        TranscriptService transcriptService = new TranscriptService();
        System.out.println(transcriptService.findGrade("s-1001"));
    }
}
