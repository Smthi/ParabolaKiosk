package order;

/**
 * IceCreamDecorator — abstract decorator in the Decorator pattern.
 * Wraps an IceCream object and delegates calls to it by default.
 * Concrete decorators override getDescription() and getPrice() to add their contribution.
 */
public abstract class IceCreamDecorator extends IceCream {

    protected final IceCream iceCream;

    public IceCreamDecorator(IceCream iceCream) {
        this.iceCream = iceCream;
    }

    @Override
    public String getDescription() {
        return iceCream.getDescription();
    }

    @Override
    public double getPrice() {
        return iceCream.getPrice();
    }
}
