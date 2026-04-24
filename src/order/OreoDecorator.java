package order;

/**
 * OreoDecorator — adds Oreo to the ice cream order (+฿5).
 */
public class OreoDecorator extends IceCreamDecorator {

    public OreoDecorator(IceCream iceCream) {
        super(iceCream);
    }

    @Override
    public String getDescription() {
        return iceCream.getDescription() + " + Oreo";
    }

    @Override
    public double getPrice() {
        return iceCream.getPrice() + 5.0;
    }
}