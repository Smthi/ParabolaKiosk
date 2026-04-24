package order;

/**
 * BiscoffDecorator — adds biscoff to the ice cream order (+฿10).
 */
public class BiscoffDecorator extends IceCreamDecorator {

    public BiscoffDecorator(IceCream iceCream) {
        super(iceCream);
    }

    @Override
    public String getDescription() {
        return iceCream.getDescription() + " + Biscoff";
    }

    @Override
    public double getPrice() {
        return iceCream.getPrice() + 10.0;
    }
}