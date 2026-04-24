package observer;

/**
 * Subject interface — part of the Observer design pattern.
 * Classes that generate events (like Inventory) implement this.
 */
public interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(String flavorName, int remainingStock);
}
