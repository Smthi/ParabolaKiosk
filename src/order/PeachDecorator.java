package order;

/**
 * PeachDecorator — adds peach to the ice cream order (+฿5).
 */
public class PeachDecorator extends IceCreamDecorator {

    public PeachDecorator(IceCream iceCream) {
        super(iceCream);
    }

    @Override
    public String getDescription() {
        return iceCream.getDescription() + " + Peach";
    }

    @Override
    public double getPrice() {
        return iceCream.getPrice() + 5.0;
    }
}