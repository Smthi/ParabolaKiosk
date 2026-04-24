package order;

/**
 * ToppingDecorator — generic concrete decorator for any topping.
 *
 * Instead of one class per topping, this single parameterised decorator
 * handles all nine toppings while still demonstrating the Decorator pattern.
 *
 * Standard toppings: ฿5  (Red Jelly, Oreo, Cornflakes, Honey Star,
 *                         White Malt, Coco Powder, Strawberry, Peach)
 * Premium topping:   ฿10 (Biscoff)
 *
 * For Large orders the first two toppings are free — this is enforced
 * by passing price=0 for those slots, which is handled by KioskApiHandler
 * before the decorator chain is built.
 */
public class ToppingDecorator extends IceCreamDecorator {

    private final String toppingName;
    private final double toppingPrice;

    public ToppingDecorator(IceCream iceCream, String toppingName, double toppingPrice) {
        super(iceCream);
        this.toppingName  = toppingName;
        this.toppingPrice = toppingPrice;
    }

    @Override
    public String getDescription() {
        return iceCream.getDescription() + " + " + toppingName;
    }

    @Override
    public double getPrice() {
        return iceCream.getPrice() + toppingPrice;
    }

    public String getToppingName()  { return toppingName; }
    public double getToppingPrice() { return toppingPrice; }
}
