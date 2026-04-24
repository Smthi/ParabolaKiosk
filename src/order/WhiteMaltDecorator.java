package order;

/**
 * WhiteMaltDecorator — adds white malt to the ice cream order (+฿5).
 */
public class WhiteMaltDecorator extends IceCreamDecorator {

    public WhiteMaltDecorator(IceCream iceCream) {
        super(iceCream);
    }

    @Override
    public String getDescription() {
        return iceCream.getDescription() + " + White Malt";
    }

    @Override
    public double getPrice() {
        return iceCream.getPrice() + 5.0;
    }
}