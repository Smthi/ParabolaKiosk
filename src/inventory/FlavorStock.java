package inventory;

/**
 * FlavorStock — encapsulates the quantity and status of a single ice cream flavor.
 */
public class FlavorStock {

    private final String flavorName;
    private int quantity;
    private final int lowStockThreshold;

    public FlavorStock(String flavorName, int quantity) {
        this(flavorName, quantity, 3);
    }

    public FlavorStock(String flavorName, int quantity, int lowStockThreshold) {
        this.flavorName = flavorName;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
    }

    /** Deducts one serving. Returns true if successful. */
    public boolean deduct() {
        if (quantity > 0) {
            quantity--;
            return true;
        }
        return false;
    }

    public void restock(int amount) {
        quantity += amount;
    }

    public boolean isLowStock()    { return quantity <= lowStockThreshold && quantity > 0; }
    public boolean isOutOfStock()  { return quantity <= 0; }

    public String getFlavorName()      { return flavorName; }
    public int    getQuantity()        { return quantity; }
    public int    getLowStockThreshold() { return lowStockThreshold; }
}
