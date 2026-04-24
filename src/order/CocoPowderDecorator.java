package order;

/**
 * CocoPowderDecorator — adds coco powder to the ice cream order (+฿5).
 */
public class CocoPowderDecorator extends IceCreamDecorator {

    public CocoPowderDecorator(IceCream iceCream) {
        super(iceCream);
    }

    @Override
    public String getDescription() {
        return iceCream.getDescription() + " + Coco Powder";
    }

    @Override
    public double getPrice() {
        return iceCream.getPrice() + 5.0;
    }
}