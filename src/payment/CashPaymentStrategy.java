package payment;

/**
 * CashPaymentStrategy — Strategy concrete implementation.
 * Accepts physical cash, validates the tendered amount, and calculates change.
 */
public class CashPaymentStrategy implements PaymentStrategy {

    private final double cashTendered;

    public CashPaymentStrategy(double cashTendered) {
        this.cashTendered = cashTendered;
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("Payment: Cash");
        System.out.printf ("Tendered: ฿%.2f%n", cashTendered);

        if (cashTendered >= amount) {
            double change = cashTendered - amount;
            System.out.printf("Change : ฿%.2f%n", change);
            System.out.println("Cash payment successful.");
            return true;
        } else {
            System.out.printf("Insufficient cash. Please insert ฿%.2f more.%n", amount - cashTendered);
            return false;
        }
    }

    @Override
    public String getPaymentMethod() { return "Cash"; }

    public double getCashTendered() { return cashTendered; }
}
