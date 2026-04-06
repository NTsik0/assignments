package badsmells;

/*
 Smell: Insider Trading

 ORIGINAL PROBLEM:
 AuditService accessed BankAccount.balance and BankAccount.secretFlag directly.
 The two classes shared too much knowledge of each other's internals, creating
 tight coupling: any rename or restructuring of BankAccount fields would break
 AuditService.

 REFACTORINGS APPLIED:
 1. Encapsulate Fields, balance and status are now private in BankAccount.
 2. Move Method, the freeze decision belongs to BankAccount because only it
    knows its own balance; freezeIfNegativeBalance() moves there.
 3. AuditService is simplified to a thin caller that asks the account to
    perform the check rather than inspecting internals.

 WHY BETTER:
 BankAccount owns and protects its own state. AuditService no longer needs to
 know what field names exist inside the account. Adding new freeze conditions
 requires changing only BankAccount.
 */
public class InsiderTradingExample {

    static class BankAccount {
        private double balance;
        private String status = "ACTIVE";

        BankAccount(double balance) {
            this.balance = balance;
        }

        public void freezeIfNegativeBalance() {
            if (balance < 0) {
                status = "FROZEN";
            }
        }

        public String getStatus() { return status; }
    }

    static class AuditService {
        public void audit(BankAccount account) {
            account.freezeIfNegativeBalance();
        }
    }

    public void clientCode() {
        BankAccount  account      = new BankAccount(-50);
        AuditService auditService = new AuditService();
        auditService.audit(account);
        System.out.println(account.getStatus());
    }
}
