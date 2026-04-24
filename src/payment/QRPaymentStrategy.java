package payment;

import java.util.UUID;

/**
 * QRPaymentStrategy — Strategy concrete implementation.
 * Simulates a Thai PromptPay / QR code payment with a generated reference number.
 */
public class QRPaymentStrategy implements PaymentStrategy {

    private final String phoneNumber;

    public QRPaymentStrategy(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean pay(double amount) {
        String refCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.println("Payment: QR PromptPay");
        System.out.printf ("Amount : ฿%.2f%n", amount);
        System.out.println("Ref No.: " + refCode);
        System.out.println("Scanning QR code... Please confirm on your banking app.");
        try { Thread.sleep(400); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        System.out.println("Payment confirmed via PromptPay (" + phoneNumber + ").");
        return true;
    }

    @Override
    public String getPaymentMethod() { return "QR PromptPay"; }

    public String getPhoneNumber() { return phoneNumber; }
}
