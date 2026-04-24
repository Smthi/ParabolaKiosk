package order;

/**
 * RedJellyDecorator — adds red jelly to the ice cream order (+฿5).
 */
public class RedJellyDecorator extends IceCreamDecorator {

    public RedJellyDecorator(IceCream iceCream) {
        super(iceCream);
    }

    @Override
    public String getDescription() {
        return iceCream.getDescription() + " + Red Jelly";
    }

    @Override
    public double getPrice() {
        return iceCream.getPrice() + 5.0;
    }
}