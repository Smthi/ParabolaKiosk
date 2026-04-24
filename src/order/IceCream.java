package order;

/**
 * IceCream — abstract component in the Decorator pattern.
 * All base ice creams and decorators extend this class.
 */
public abstract class IceCream {
    public abstract String getDescription();
    public abstract double getPrice();

    @Override
    public String toString() {
        return getDescription() + " — ฿" + String.format("%.2f", getPrice());
    }
}
