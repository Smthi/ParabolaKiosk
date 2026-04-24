import machine.KioskMachine;
import machine.KioskServer;
import order.*;
import payment.*;

/**
 * Main — entry point for the Parabola Kiosk System.
 *
 * Demonstrates all four design patterns through scripted orders,
 * then starts the HTTP server so the HTML UI can be used live.
 *
 * MENU:
 *   Flavours : Yogurt (always) | Milk or Dark Chocolate (daily rotation) | Two-tone (always)
 *   Sizes    : Small ฿25 | Large ฿35 (2 free toppings)
 *   Toppings : Red Jelly ฿5 | Oreo ฿5 | Cornflakes ฿5 | Honey Star ฿5 | Biscoff ฿10
 *              White Malt ฿5 | Coco Powder ฿5 | Strawberry ฿5 | Peach ฿5
 *
 * HOW TO RUN:
 *   javac -d out -sourcepath src src/Main.java
 *   java  -cp out Main
 *   → Open parabola-kiosk.html in browser
 */
public class Main {

    public static void main(String[] args) throws Exception {

        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║         PARABOLA KIOSK SYSTEM  — Startup          ║");
        System.out.println("║         University Campus Ice Cream Kiosk          ║");
        System.out.println("╚════════════════════════════════════════════════════╝");

        // ── Singleton: one machine instance ───────────────────────────────
        KioskMachine machine = KioskMachine.getInstance();
        System.out.println("\n[MACHINE]: Parabola Kiosk is READY.");
        System.out.println("[MACHINE]: Today's rotation flavor → " + machine.getTodayRotationFlavor());
        machine.printInventoryStatus();

        String todayFlavor = machine.getTodayRotationFlavor();

        // ══════════════════════════════════════════════════════════════════
        //  ORDER 1 — Yogurt Large + Oreo (free) + Strawberry (free)  [Cash]
        //  Shows: Decorator (2 free toppings for Large) + Strategy (Cash)
        // ══════════════════════════════════════════════════════════════════
        System.out.println("\n>>> Customer 1: Yogurt Large + Oreo (FREE) + Strawberry (FREE)  Cash ฿50");
        IceCream c1 = new ToppingDecorator(
                          new ToppingDecorator(
                            new BaseIceCream("Yogurt", "large"),
                            "Oreo", 0.0),         // free slot 1
                          "Strawberry", 0.0);     // free slot 2
        machine.placeOrder(c1, new CashPaymentStrategy(50.0), "Yogurt");

        // ══════════════════════════════════════════════════════════════════
        //  ORDER 2 — Today's rotation Large + Biscoff (free) + Oreo (free) + Peach (+฿5)  [QR]
        //  Shows: Decorator (2 free + 1 paid) + Strategy (QR)
        // ══════════════════════════════════════════════════════════════════
        System.out.println("\n>>> Customer 2: " + todayFlavor + " Large + Biscoff(FREE) + Oreo(FREE) + Peach(+฿5)  QR");
        IceCream c2 = new ToppingDecorator(
                          new ToppingDecorator(
                            new ToppingDecorator(
                              new BaseIceCream(todayFlavor, "large"),
                              "Biscoff", 0.0),    // free slot 1
                            "Oreo", 0.0),         // free slot 2
                          "Peach", 5.0);          // paid
        machine.placeOrder(c2, new QRPaymentStrategy("081-234-5678"), todayFlavor);

        // ══════════════════════════════════════════════════════════════════
        //  ORDER 3 — Two-tone Small + Biscoff (+฿10) + Honey Star (+฿5)  [Cash]
        //  Shows: Decorator (no free toppings for Small)
        // ══════════════════════════════════════════════════════════════════
        System.out.println("\n>>> Customer 3: Two-tone Small + Biscoff(+฿10) + Honey Star(+฿5)  Cash ฿50");
        IceCream c3 = new ToppingDecorator(
                          new ToppingDecorator(
                            new BaseIceCream("TwoTone", "small"),
                            "Biscoff", 10.0),
                          "Honey Star", 5.0);
        machine.placeOrder(c3, new CashPaymentStrategy(50.0), "TwoTone");

        // ══════════════════════════════════════════════════════════════════
        //  ORDERS 4-7 — Drain Yogurt to trigger Observer alerts
        // ══════════════════════════════════════════════════════════════════
        System.out.println("\n>>> Customers 4-7: Draining Yogurt stock to trigger Observer...");
        for (int i = 4; i <= 7; i++) {
            IceCream ci = new ToppingDecorator(
                              new BaseIceCream("Yogurt", "small"),
                              "Cornflakes", 5.0);
            machine.placeOrder(ci, new CashPaymentStrategy(35.0), "Yogurt");
        }

        // ══════════════════════════════════════════════════════════════════
        //  DEMO — Insufficient cash
        // ══════════════════════════════════════════════════════════════════
        System.out.println("\n>>> Customer 8: Yogurt Large + Oreo + Biscoff (Cash ฿20 — insufficient)");
        IceCream c8 = new ToppingDecorator(
                          new ToppingDecorator(
                            new BaseIceCream("Yogurt", "large"),
                            "Oreo", 0.0),
                          "Biscoff", 0.0);
        machine.placeOrder(c8, new CashPaymentStrategy(20.0), "Yogurt");

        machine.printInventoryStatus();

        // ── Start HTTP server ──────────────────────────────────────────────
        System.out.println("\n[STARTING KIOSK UI SERVER...]");
        KioskServer server = new KioskServer();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        Thread.currentThread().join();
    }
}
