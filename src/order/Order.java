package order;

import payment.PaymentStrategy;

/**
 * Order — holds one ice cream (fully decorated) and a payment strategy.
 * Calling process() runs the payment and prints the receipt.
 */
public class Order {

    private static int orderCounter = 1;

    private final int orderId;
    private final IceCream iceCream;
    private final PaymentStrategy paymentStrategy;
    private String status;

    public Order(IceCream iceCream, PaymentStrategy paymentStrategy) {
        this.orderId        = orderCounter++;
        this.iceCream       = iceCream;
        this.paymentStrategy = paymentStrategy;
        this.status         = "PENDING";
    }

    /** Processes the order: prints receipt and runs payment. */
    public void process() {
        System.out.println("\n--- Order #" + orderId + " ---");
        System.out.println("Item   : " + iceCream.getDescription());
        System.out.printf ("Total  : ฿%.2f%n", iceCream.getPrice());

        boolean paid = paymentStrategy.pay(iceCream.getPrice());
        this.status = paid ? "COMPLETED" : "FAILED";
        System.out.println("Status : " + status);
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public int       getOrderId()  { return orderId; }
    public IceCream  getIceCream() { return iceCream; }
    public String    getStatus()   { return status; }
    public double    getTotal()    { return iceCream.getPrice(); }
}
