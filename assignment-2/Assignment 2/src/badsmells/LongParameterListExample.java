package badsmells;

/*
 Smell: Long Parameter List

 ORIGINAL PROBLEM:
 registerStudent() took 12 parameters. Call sites were unreadable, argument
 order was easy to mix up, and every related group of values (address,
 guardian contact, enrollment details) had to be passed separately each time.

 REFACTORINGS APPLIED:
 1. Introduce Parameter Object:
    Address          (city, street, zipCode)
    GuardianContact  (guardianName, guardianPhone)
    EnrollmentInfo   (program, startYear, scholarship)
 2. Change Method Signature, registerStudent() now takes four objects plus
    the two personal scalars that belong to no group.

 WHY BETTER:
 Each parameter object gives the group a name and a place for future
 validation. Call sites are readable, each argument block is self-describing.
 Adding a field to Address touches only Address and its callers, not the
 registration method signature.
 */
public class LongParameterListExample {

    static class Address {
        final String city;
        final String street;
        final String zipCode;

        Address(String city, String street, String zipCode) {
            this.city    = city;
            this.street  = street;
            this.zipCode = zipCode;
        }
    }

    static class GuardianContact {
        final String name;
        final String phone;

        GuardianContact(String name, String phone) {
            this.name  = name;
            this.phone = phone;
        }
    }

    static class EnrollmentInfo {
        final String  program;
        final int     startYear;
        final boolean scholarship;

        EnrollmentInfo(String program, int startYear, boolean scholarship) {
            this.program     = program;
            this.startYear   = startYear;
            this.scholarship = scholarship;
        }
    }

    public String registerStudent(String firstName, String lastName, String email, String phone,
                                   Address address, GuardianContact guardian, EnrollmentInfo enrollment) {
        return firstName + " " + lastName
            + " -> " + enrollment.program + " (" + enrollment.startYear + ")"
            + ", guardian=" + guardian.name
            + ", scholarship=" + enrollment.scholarship
            + ", address=" + address.city + ", " + address.street + ", " + address.zipCode
            + ", contact=" + email + "/" + phone
            + ", guardianPhone=" + guardian.phone;
    }

    public void clientCode() {
        Address        address    = new Address("Tbilisi", "Rustaveli Ave 10", "0108");
        GuardianContact guardian  = new GuardianContact("Maka Beridze", "+995-555-000-999");
        EnrollmentInfo enrollment = new EnrollmentInfo("Computer Science", 2026, true);

        String summary = registerStudent(
            "Nino", "Beridze", "nino@example.com", "+995-555-000-001",
            address, guardian, enrollment
        );
        System.out.println(summary);
    }
}
