package observer;

/**
 * Observer interface — part of the Observer design pattern.
 * Any class that wants to be notified of inventory changes must implement this.
 */
public interface Observer {
    void update(String flavorName, int remainingStock);
}
