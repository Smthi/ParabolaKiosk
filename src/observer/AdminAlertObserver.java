package observer;

/**
 * AdminAlertObserver — Concrete Observer.
 * Receives low-stock / sold-out notifications and prints admin alerts to the console.
 */
public class AdminAlertObserver implements Observer {

    private String adminName;

    public AdminAlertObserver(String adminName) {
        this.adminName = adminName;
    }

    @Override
    public void update(String flavorName, int remainingStock) {
        if (remainingStock <= 0) {
            System.out.println("\n[ADMIN ALERT - " + adminName + "]: *** "
                    + flavorName + " is SOLD OUT! Please refill immediately. ***");
        } else {
            System.out.println("\n[ADMIN ALERT - " + adminName + "]: Warning! "
                    + flavorName + " is running LOW — only " + remainingStock + " serving(s) left.");
        }
    }

    public String getAdminName() {
        return adminName;
    }
}
