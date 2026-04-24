package payment;

/**
 * PaymentStrategy interface — Strategy design pattern.
 * Defines the contract for all payment methods used at the kiosk.
 */
public interface PaymentStrategy {
    /**
     * Processes a payment for the given amount.
     * @return true if payment succeeds, false otherwise.
     */
    boolean pay(double amount);

    /** Human-readable name of this payment method. */
    String getPaymentMethod();
}
