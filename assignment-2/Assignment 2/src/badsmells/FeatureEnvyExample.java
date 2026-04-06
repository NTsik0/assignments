package badsmells;

/*
 Smell: Feature Envy

 ORIGINAL PROBLEM:
 ScholarshipCalculator.qualifies() only accessed getters on StudentAccount.
 It had no data of its own and was entirely interested in another object's
 state, which is the hallmark of Feature Envy.

 REFACTORINGS APPLIED:
 1. Move Method, qualifiesForScholarship() moves into StudentAccount, where
    the relevant data already lives.
 2. ScholarshipCalculator is removed; it added no value of its own.

 WHY BETTER:
 The logic sits next to the data it depends on. The client can ask the account
 directly, which is more natural and removes an unnecessary class. If the
 scholarship threshold changes, only StudentAccount needs to change.
 */
public class FeatureEnvyExample {

    static class StudentAccount {
        private final int    completedCredits;
        private final double gpa;

        StudentAccount(int completedCredits, double gpa) {
            this.completedCredits = completedCredits;
            this.gpa              = gpa;
        }

        public boolean qualifiesForScholarship() {
            return completedCredits >= 30 && gpa >= 3.7;
        }
    }

    public void clientCode() {
        StudentAccount account = new StudentAccount(36, 3.9);
        System.out.println(account.qualifiesForScholarship());
    }
}
