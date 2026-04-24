package order;

/**
 * BaseIceCream — the concrete component in the Decorator pattern.
 * Price is determined by SIZE (Small ฿25 / Large ฿35), not by flavor.
 */
public class BaseIceCream extends IceCream {

    private final String flavor;
    private final String size;     // "small" or "large"
    private final double basePrice;

    public BaseIceCream(String flavor, String size) {
        this.flavor    = flavor;
        this.size      = size.toLowerCase();
        this.basePrice = this.size.equals("small") ? 25.0 : 35.0;
    }

    /** Convenience constructor — defaults to small. */
    public BaseIceCream(String flavor) {
        this(flavor, "small");
    }

    @Override
    public String getDescription() {
        String sizeLabel = size.equals("small") ? "Small Cup" : "Large Cup";
        return flavor + " (" + sizeLabel + ")";
    }

    @Override
    public double getPrice() {
        return basePrice;
    }

    public String getFlavor() { return flavor; }
    public String getSize()   { return size; }
}
