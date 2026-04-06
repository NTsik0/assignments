package badsmells;

/*
 Smell: Comments

 ORIGINAL PROBLEM:
 Three block comments were needed to explain three separate concerns inside one
 method: VIP discount, bulk discount, and tax. The comments were compensating
 for code that lacked self-explanatory structure.

 REFACTORINGS APPLIED:
 1. Extract Method, each concern becomes its own intention-revealing method.
 2. Rename, the top-level method now reads like a plain-English sentence.

 WHY BETTER:
 The extracted method names (applyVipDiscount, applyBulkDiscount, applyTax)
 replace the comments entirely. The body of priceWithDiscountsAndTax is a
 four-line narrative that any reader can follow without annotations.
 */
public class CommentsExample {

    public double priceWithDiscountsAndTax(double basePrice, boolean vip, int quantity) {
        double price = basePrice;
        price = applyVipDiscount(price, vip);
        price = applyBulkDiscount(price, quantity);
        price = applyTax(price);
        return price;
    }

    private double applyVipDiscount(double price, boolean vip) {
        return vip ? price * 0.90 : price;
    }

    private double applyBulkDiscount(double price, int quantity) {
        return quantity > 20 ? price * 0.95 : price;
    }

    private double applyTax(double price) {
        return price * 1.18;
    }

    public void clientCode() {
        double vipOrder     = priceWithDiscountsAndTax(120, true,  25);
        double regularOrder = priceWithDiscountsAndTax(120, false,  5);
        System.out.println(vipOrder);
        System.out.println(regularOrder);
    }
}
