package order;

/**
 * StrawberryDecorator — adds strawberry to the ice cream order (+฿5).
 */
public class StrawberryDecorator extends IceCreamDecorator {

    public StrawberryDecorator(IceCream iceCream) {
        super(iceCream);
    }

    @Override
    public String getDescription() {
        return iceCream.getDescription() + " + Strawberry";
    }

    @Override
    public double getPrice() {
        return iceCream.getPrice() + 5.0;
    }
}