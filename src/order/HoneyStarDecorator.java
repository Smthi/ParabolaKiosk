package order;

/**
 * HoneyStarDecorator — adds honey star to the ice cream order (+฿5).
 */
public class HoneyStarDecorator extends IceCreamDecorator {

    public HoneyStarDecorator(IceCream iceCream) {
        super(iceCream);
    }

    @Override
    public String getDescription() {
        return iceCream.getDescription() + " + Honey Star";
    }

    @Override
    public double getPrice() {
        return iceCream.getPrice() + 5.0;
    }
}