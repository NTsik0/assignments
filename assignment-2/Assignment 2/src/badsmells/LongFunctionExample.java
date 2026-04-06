package badsmells;

/*
 Smell: Long Function

 ORIGINAL PROBLEM:
 processOrder() mixed five distinct concerns in one 40-line block: discount
 calculation, shipping calculation, tax calculation, approval-status
 derivation, and summary formatting. Readers had to hold all five in their
 heads at once to understand any one of them.

 REFACTORINGS APPLIED:
 1. Extract Method for each concern:
    calculateDiscount, calculateShipping, calculateTax, deriveApprovalStatus,
    buildSummary.
 2. Extract Local Variable, subtotal named early and passed explicitly.
 3. processOrder() is now a five-line orchestrator whose body reads like a
    plain-English description of the algorithm.

 WHY BETTER:
 Each extracted method can be read, tested, and changed independently.
 The top-level method communicates the overall structure at a glance.
 */
public class LongFunctionExample {

    public String processOrder(String customerType, int quantity, double price, boolean expressDelivery) {
        double subtotal = quantity * price;
        double discount = calculateDiscount(customerType, subtotal);
        double shipping = calculateShipping(expressDelivery, quantity);
        double tax      = calculateTax(subtotal, discount);
        double total    = subtotal - discount + shipping + tax;
        String status   = deriveApprovalStatus(total);
        return buildSummary(customerType, quantity, price, subtotal, discount, shipping, tax, total, status);
    }

    private double calculateDiscount(String customerType, double subtotal) {
        switch (customerType) {
            case "STUDENT":       return subtotal * 0.05;
            case "VIP":           return subtotal * 0.12;
            case "EMPLOYEE":      return subtotal * 0.20;
            default:              return 0;
        }
    }

    private double calculateShipping(boolean expressDelivery, int quantity) {
        if (expressDelivery) {
            return quantity > 10 ? 35 : 25;
        }
        return quantity > 10 ? 15 : 10;
    }

    private double calculateTax(double subtotal, double discount) {
        return (subtotal - discount) * 0.18;
    }

    private String deriveApprovalStatus(double total) {
        if (total > 500) return "MANAGER_APPROVAL";
        if (total > 200) return "FINANCE_REVIEW";
        return "AUTO_APPROVED";
    }

    private String buildSummary(String customerType, int quantity, double price,
                                 double subtotal, double discount, double shipping,
                                 double tax, double total, String status) {
        return "customerType=" + customerType + '\n'
             + "quantity="     + quantity     + '\n'
             + "price="        + price        + '\n'
             + "subtotal="     + subtotal     + '\n'
             + "discount="     + discount     + '\n'
             + "shipping="     + shipping     + '\n'
             + "tax="          + tax          + '\n'
             + "total="        + total        + '\n'
             + "status="       + status;
    }

    public void clientCode() {
        System.out.println(processOrder("VIP", 12, 30, true));
        System.out.println(processOrder("STUDENT", 2, 50, false));
    }
}
