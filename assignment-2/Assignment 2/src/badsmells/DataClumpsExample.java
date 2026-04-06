package badsmells;

/*
 Smell: Data Clumps

 ORIGINAL PROBLEM:
 Every method and every call site passed (name, email, phone) as three
 separate arguments. The same trio appeared nine times across four methods and
 twelve lines of client code.

 REFACTORINGS APPLIED:
 1. Introduce Parameter Object (ContactInfo), the clump becomes one class.
 2. Move Method, behavior that depends only on ContactInfo moves into it.
 3. Change Method Signature, all methods now accept a ContactInfo.

 WHY BETTER:
 Each piece of contact-related logic now lives in one place. Adding a fax
 number to a contact is a single-field change in ContactInfo, not a signature
 change across four methods and every call site.
 */
public class DataClumpsExample {

    static class ContactInfo {
        private final String name;
        private final String email;
        private final String phone;

        public ContactInfo(String name, String email, String phone) {
            this.name  = name;
            this.email = email;
            this.phone = phone;
        }

        public String label() {
            return name + " <" + email + ">, phone: " + phone;
        }

        public String emailGreeting() {
            return "To: " + email + ", hello " + name;
        }

        public String smsMessage() {
            return "SMS to " + phone + ": Hi " + name;
        }

        public boolean isReachable() {
            return email != null && !email.trim().isEmpty()
                && phone != null && !phone.trim().isEmpty();
        }
    }

    public void clientCode() {
        ContactInfo student    = new ContactInfo("Nino",   "nino@example.com",   "+995-555-000-001");
        ContactInfo advisor    = new ContactInfo("Giorgi", "giorgi@example.com",  "+995-555-000-002");
        ContactInfo accountant = new ContactInfo("Maka",   "maka@example.com",    "+995-555-000-003");

        for (ContactInfo contact : new ContactInfo[]{student, advisor, accountant}) {
            System.out.println(contact.label());
            System.out.println(contact.emailGreeting());
            System.out.println(contact.smsMessage());
            System.out.println(contact.isReachable());
        }
    }
}
