package observer;

import java.util.HashMap;
import java.util.Map;

/**
 * KioskDisplayObserver — Concrete Observer.
 * Updates an internal status map used by the HTML UI to mark flavors as low/sold-out.
 */
public class KioskDisplayObserver implements Observer {

    private final Map<String, String> displayStatus = new HashMap<>();

    @Override
    public void update(String flavorName, int remainingStock) {
        if (remainingStock <= 0) {
            displayStatus.put(flavorName, "SOLD_OUT");
            System.out.println("[KIOSK DISPLAY]: " + flavorName + " marked as SOLD OUT on kiosk screen.");
        } else {
            displayStatus.put(flavorName, "LOW_STOCK");
            System.out.println("[KIOSK DISPLAY]: " + flavorName
                    + " display updated — LOW STOCK (" + remainingStock + " left).");
        }
    }

    /** Returns the display status for a specific flavor. */
    public String getStatus(String flavorName) {
        return displayStatus.getOrDefault(flavorName, "AVAILABLE");
    }

    /** Returns the full status map (used by the API handler). */
    public Map<String, String> getAllStatuses() {
        return displayStatus;
    }
}
