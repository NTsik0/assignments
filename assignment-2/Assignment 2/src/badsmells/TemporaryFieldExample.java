package badsmells;

/*
 Smell: Temporary Field

 ORIGINAL PROBLEM:
 examRoom and onlineMeetingLink were both fields on one class, but only one
 was ever meaningful at a time. The object carried dead state depending on
 which method had been called. Printing both at the end exposed the confused
 state: one was always null.

 REFACTORINGS APPLIED:
 1. Extract Class, OnsiteExam and OnlineExam each own only the field
    relevant to their mode.
 2. Move Method, the preparation logic moves into the appropriate class.
 3. The original class becomes a thin factory/coordinator that delegates to
    the right mode object.

 WHY BETTER:
 Each class is always in a fully consistent state: OnsiteExam always has a
 room, OnlineExam always has a link. There is no null field and no conditional
 checking which mode is active. New exam modes can be added without touching
 existing classes.
 */
public class TemporaryFieldExample {

    static class OnsiteExam {
        private final String room;

        OnsiteExam() {
            this.room = "B-204";
        }

        public String prepare() {
            return "Use room " + room;
        }
    }

    static class OnlineExam {
        private final String meetingLink;

        OnlineExam() {
            this.meetingLink = "https://meet.example/exam";
        }

        public String prepare() {
            return "Join " + meetingLink;
        }
    }

    public void clientCode() {
        OnsiteExam onsiteExam = new OnsiteExam();
        OnlineExam onlineExam = new OnlineExam();

        System.out.println(onsiteExam.prepare());
        System.out.println(onlineExam.prepare());
    }
}
