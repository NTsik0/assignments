package badsmells;

/*
 Smell: Speculative Generality

 ORIGINAL PROBLEM:
 NotificationChannel.send() declared four parameters: message, futureTemplate,
 encrypted, urgent, but EmailChannel ignored three of them entirely. The
 extra parameters were speculative: they prepared for variation that had not
 arrived and added noise to every call site.

 REFACTORINGS APPLIED:
 1. Change Method Signature, remove the three unused parameters (futureTemplate,
    encrypted, urgent) from the interface and the implementation.
 2. Inline simplification, the interface is kept because there is a real
    abstraction (channel), but it is trimmed to what is actually used.

 WHY BETTER:
 Every call site passes only the data that is actually consumed. Readers no
 longer wonder what futureTemplate or encrypted mean or why they are always
 hard-coded. If encryption is genuinely needed later, it can be added then
 with a real implementation behind it.
 */
public class SpeculativeGeneralityExample {

    interface NotificationChannel {
        void send(String message);
    }

    static class EmailChannel implements NotificationChannel {
        @Override
        public void send(String message) {
            System.out.println(message);
        }
    }

    public void clientCode() {
        NotificationChannel channel = new EmailChannel();
        channel.send("Exam starts at 10:00");
    }
}
