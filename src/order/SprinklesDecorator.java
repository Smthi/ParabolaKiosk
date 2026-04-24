package order;

/**
 * SprinklesDecorator — adds rainbow sprinkles to the ice cream order (+฿5).
 */
public class SprinklesDecorator extends IceCreamDecorator {

    public SprinklesDecorator(IceCream iceCream) {
        super(iceCream);
    }

    @Override
    public String getDescription() {
        return iceCream.getDescription() + " + Sprinkles";
    }

    @Override
    public double getPrice() {
        return iceCream.getPrice() + 5.0;
    }
}
