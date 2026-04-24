package order;

/**
 * CornflakesDecorator — adds cornflakes to the ice cream order (+฿5).
 */
public class CornflakesDecorator extends IceCreamDecorator {

    public CornflakesDecorator(IceCream iceCream) {
        super(iceCream);
    }

    @Override
    public String getDescription() {
        return iceCream.getDescription() + " + Cornflakes";
    }

    @Override
    public double getPrice() {
        return iceCream.getPrice() + 5.0;
    }
}