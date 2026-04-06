package badsmells;

/*
 Smell: Alternative Classes with Different Interfaces

 ORIGINAL PROBLEM:
 ZoomClassroom.beginSession() and TeamsClassroom.openMeeting() serve identical
 purposes but expose different method names. Client code cannot treat them
 uniformly and must branch or adapt between the two APIs.

 REFACTORINGS APPLIED:
 1. Extract Interface (VirtualClassroom) with a single agreed-upon method start().
 2. Rename / Change Method Signature, both implementations now override start().
 3. Use Supertype Where Possible, clientCode() talks to VirtualClassroom only.

  WHY BETTER:
 The client is now decoupled from the concrete providers. Adding a third
 platform (e.g. GoogleMeetClassroom) requires only a new implementation of the
 shared interface, with zero changes to existing client code.ll
 */
public class AlternativeClassesWithDifferentInterfacesExample {

    interface VirtualClassroom {
        void start();
    }

    static class ZoomClassroom implements VirtualClassroom {
        @Override
        public void start() {
            System.out.println("Zoom session started");
        }
    }

    static class TeamsClassroom implements VirtualClassroom {
        @Override
        public void start() {
            System.out.println("Teams meeting started");
        }
    }

    public void clientCode() {
        VirtualClassroom zoom  = new ZoomClassroom();
        VirtualClassroom teams = new TeamsClassroom();
        zoom.start();
        teams.start();
    }
}
