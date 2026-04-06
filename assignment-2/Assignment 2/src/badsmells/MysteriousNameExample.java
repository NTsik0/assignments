package badsmells;

/*
 Smell: Mysterious Name

 ORIGINAL PROBLEM:
 f(a, b, c) with locals x and y communicated nothing about purpose. A reader
 had to reverse-engineer the arithmetic to discover it computes net profit
 after costs, split between two parties.

 REFACTORINGS APPLIED:
 1. Rename Method, f → calculateEachPartyShare.
 2. Rename Parameters, a → unitsSold, b → pricePerUnit, c → totalCosts.
 3. Rename Local Variables, x → grossRevenue, y → netRevenue.
 4. Extract Method, grossRevenue and netRevenue steps are named inline via
    well-named locals (no separate method needed; the names are sufficient).

 WHY BETTER:
 Every symbol now announces its meaning. The formula can be verified by
 reading it, without reverse-engineering from scratch.
 */
public class MysteriousNameExample {

    public int calculateEachPartyShare(int unitsSold, int pricePerUnit, int totalCosts) {
        int grossRevenue = unitsSold * pricePerUnit;
        int netRevenue   = grossRevenue - totalCosts;
        return netRevenue / 2;
    }

    public void clientCode() {
        int result = calculateEachPartyShare(8, 4, 6);
        System.out.println(result);
    }
}
