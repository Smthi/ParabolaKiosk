package machine;

import inventory.FlavorStock;
import inventory.Inventory;
import observer.AdminAlertObserver;
import observer.KioskDisplayObserver;
import order.IceCream;
import order.Order;
import payment.PaymentStrategy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * KioskMachine — Singleton design pattern.
 *
 * Represents the one physical Parabola kiosk unit on campus.
 * There can only ever be one instance; all parts of the system
 * access it via getInstance().
 *
 * Flavours available:
 *   - Yogurt    (always available)
 *   - Milk      (available on even day-of-year)
 *   - DarkChocolate (available on odd day-of-year)
 *   - TwoTone   (always available — swirl of both)
 *
 * Sizes: Small (฿25) | Large (฿35 + 2 free toppings)
 */
public class KioskMachine {

    // ── Singleton ──────────────────────────────────────────────────────────
    private static KioskMachine instance;

    public static KioskMachine getInstance() {
        if (instance == null) instance = new KioskMachine();
        return instance;
    }

    public static void resetInstance() { instance = null; }

    // ── State ──────────────────────────────────────────────────────────────
    public enum MachineState { READY, DISPENSING, OUT_OF_STOCK }

    private MachineState state;
    private final Inventory inventory;
    private final List<Order> completedOrders;
    private final KioskDisplayObserver displayObserver;

    // ── Constructor ────────────────────────────────────────────────────────
    private KioskMachine() {
        this.state           = MachineState.READY;
        this.inventory       = new Inventory();
        this.completedOrders = new ArrayList<>();
        this.displayObserver = new KioskDisplayObserver();

        inventory.addObserver(new AdminAlertObserver("Admin"));
        inventory.addObserver(displayObserver);

        // Seed stock — each flavour gets 10 servings
        inventory.addFlavor(new FlavorStock("Yogurt",        10));
        inventory.addFlavor(new FlavorStock("Milk",          10));
        inventory.addFlavor(new FlavorStock("DarkChocolate", 10));
        inventory.addFlavor(new FlavorStock("TwoTone",       10));
    }

    // ── Daily rotation ─────────────────────────────────────────────────────
    /** Returns "Milk" or "DarkChocolate" depending on today's day-of-year. */
    public String getTodayRotationFlavor() {
        int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        return dayOfYear % 2 == 0 ? "Milk" : "DarkChocolate";
    }

    /** Returns true if the given flavor is available today (considering rotation). */
    public boolean isFlavorAvailableToday(String flavor) {
        switch (flavor) {
            case "Yogurt":
            case "TwoTone":  return true;
            case "Milk":         return getTodayRotationFlavor().equals("Milk");
            case "DarkChocolate":return getTodayRotationFlavor().equals("DarkChocolate");
            default: return false;
        }
    }

    // ── Core operation ─────────────────────────────────────────────────────
    public Order placeOrder(IceCream iceCream, PaymentStrategy payment, String flavor) {
        if (state == MachineState.OUT_OF_STOCK) {
            System.out.println("[MACHINE]: Machine is currently out of stock.");
            return null;
        }
        if (!isFlavorAvailableToday(flavor)) {
            System.out.println("[MACHINE]: " + flavor + " is not available today.");
            return null;
        }
        if (!inventory.isAvailable(flavor)) {
            System.out.println("[MACHINE]: Sorry, " + flavor + " is sold out.");
            return null;
        }

        state = MachineState.DISPENSING;
        Order order = new Order(iceCream, payment);
        order.process();

        if ("COMPLETED".equals(order.getStatus())) {
            inventory.deductFlavor(flavor);
            completedOrders.add(order);
            System.out.println("[MACHINE]: Dispensing your ice cream... Enjoy! 🍦");
        }

        state = MachineState.READY;
        return order;
    }

    public void printInventoryStatus() {
        System.out.println("\n=== Parabola Kiosk — Inventory ===");
        String today = getTodayRotationFlavor();
        String[] order = {"Yogurt", "Milk", "DarkChocolate", "TwoTone"};
        for (String name : order) {
            FlavorStock stock = inventory.getAllStocks().get(name);
            if (stock == null) continue;
            String avail = isFlavorAvailableToday(name) ? "" : " [NOT TODAY]";
            String tag   = stock.isOutOfStock() ? "SOLD OUT"
                         : stock.isLowStock()   ? "LOW" : "OK";
            System.out.printf("  %-14s : %2d servings  [%s]%s%n",
                    name, stock.getQuantity(), tag, avail);
        }
        System.out.println("  Today's rotation: " + today);
        System.out.println("==================================");
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public Inventory            getInventory()       { return inventory; }
    public MachineState         getState()           { return state; }
    public List<Order>          getCompletedOrders() { return completedOrders; }
    public KioskDisplayObserver getDisplayObserver() { return displayObserver; }
}
