package badsmells;

/*
 Smell: Duplicated Code

 ORIGINAL PROBLEM:
 summerInvoice() and winterInvoice() shared identical tax and shipping logic.
 Only the discount calculation differed. A tax-rate change required editing
 two methods and risked the copies drifting out of sync.

 REFACTORINGS APPLIED:
 1. Extract Method, calculateTax() and calculateShipping() capture the shared
    steps in one place.
 2. The two public methods now delegate to these helpers and supply only the
    discount logic that is genuinely different for each season.

 WHY BETTER:
 Tax and shipping logic exist in exactly one method each. Season-specific
 discount rules are isolated and easy to compare side by side.
 */
public class DuplicatedCodeExample {

    public double summerInvoice(double subtotal) {
        double discount = subtotal > 200 ? subtotal * 0.10 : 0;
        return applyCharges(subtotal, discount);
    }

    public double winterInvoice(double subtotal) {
        double discount = subtotal > 200 ? subtotal * 0.20 : 50;
        return applyCharges(subtotal, discount);
    }

    private double applyCharges(double subtotal, double discount) {
        double tax      = calculateTax(subtotal);
        double shipping = calculateShipping(subtotal);
        return subtotal + tax + shipping - discount;
    }

    private double calculateTax(double subtotal) {
        return subtotal * 0.18;
    }

    private double calculateShipping(double subtotal) {
        return subtotal > 100 ? 0 : 15;
    }

    public void clientCode() {
        System.out.println(summerInvoice(240));
        System.out.println(winterInvoice(240));
    }
}
