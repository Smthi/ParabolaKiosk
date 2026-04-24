package inventory;

import observer.Observer;
import observer.Subject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Inventory — acts as the Subject in the Observer pattern.
 * Maintains flavor stock and notifies registered observers when stock is low/empty.
 */
public class Inventory implements Subject {

    private final Map<String, FlavorStock> stocks = new HashMap<>();
    private final List<Observer> observers = new ArrayList<>();

    // ── Subject interface ──────────────────────────────────────────────────────

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String flavorName, int remainingStock) {
        for (Observer observer : observers) {
            observer.update(flavorName, remainingStock);
        }
    }

    // ── Inventory operations ───────────────────────────────────────────────────

    public void addFlavor(FlavorStock stock) {
        stocks.put(stock.getFlavorName(), stock);
    }

    /**
     * Deducts one serving of the given flavor.
     * Fires observers if the resulting stock is low or zero.
     */
    public boolean deductFlavor(String flavorName) {
        FlavorStock stock = stocks.get(flavorName);
        if (stock == null || stock.isOutOfStock()) return false;

        stock.deduct();
        if (stock.isOutOfStock() || stock.isLowStock()) {
            notifyObservers(flavorName, stock.getQuantity());
        }
        return true;
    }

    public boolean isAvailable(String flavorName) {
        FlavorStock stock = stocks.get(flavorName);
        return stock != null && !stock.isOutOfStock();
    }

    public int getQuantity(String flavorName) {
        FlavorStock stock = stocks.get(flavorName);
        return stock == null ? 0 : stock.getQuantity();
    }

    public Map<String, FlavorStock> getAllStocks() {
        return stocks;
    }
}
