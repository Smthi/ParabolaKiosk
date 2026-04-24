package machine;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import inventory.FlavorStock;
import order.*;
import payment.*;

import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * KioskApiHandler — HTTP handler bridging the HTML kiosk UI and Java backend.
 *
 * Endpoints:
 *   GET  /api/menu        — flavours with stock, today's rotation, pricing
 *   GET  /api/price       — builds Decorator chain for a size + toppings combo
 *   POST /api/order       — full order: Decorator + Strategy + Observer side effects
 *   GET  /api/inventory   — raw inventory snapshot for the admin panel
 *
 * Pricing:
 *   Small cup: ฿25 base, all toppings charged
 *   Large cup: ฿35 base, first 2 toppings FREE, additional +฿5 (Biscoff +฿10)
 */
public class KioskApiHandler implements HttpHandler {

    // Topping name → base price (before free allowance logic)
    private static final Map<String, Double> TOPPING_PRICES = new LinkedHashMap<>();
    static {
        TOPPING_PRICES.put("RedJelly",    5.0);
        TOPPING_PRICES.put("Oreo",        5.0);
        TOPPING_PRICES.put("Cornflakes",  5.0);
        TOPPING_PRICES.put("HoneyStar",   5.0);
        TOPPING_PRICES.put("Biscoff",    10.0);
        TOPPING_PRICES.put("WhiteMalt",   5.0);
        TOPPING_PRICES.put("CocoPowder",  5.0);
        TOPPING_PRICES.put("Strawberry",  5.0);
        TOPPING_PRICES.put("Peach",       5.0);
    }

    private static final Map<String, String> TOPPING_DISPLAY = new LinkedHashMap<>();
    static {
        TOPPING_DISPLAY.put("RedJelly",   "Red Jelly");
        TOPPING_DISPLAY.put("Oreo",       "Oreo");
        TOPPING_DISPLAY.put("Cornflakes", "Cornflakes");
        TOPPING_DISPLAY.put("HoneyStar",  "Honey Star");
        TOPPING_DISPLAY.put("Biscoff",    "Biscoff");
        TOPPING_DISPLAY.put("WhiteMalt",  "White Malt");
        TOPPING_DISPLAY.put("CocoPowder", "Coco Powder");
        TOPPING_DISPLAY.put("Strawberry", "Strawberry");
        TOPPING_DISPLAY.put("Peach",      "Peach");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin",  "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        URI    uri    = exchange.getRequestURI();
        String path   = uri.getPath();
        String method = exchange.getRequestMethod();
        String response;
        int    status = 200;

        try {
            if      (path.startsWith("/api/menu")      && "GET".equals(method))  response = handleMenu();
            else if (path.startsWith("/api/price")     && "GET".equals(method))  response = handlePrice(uri.getQuery());
            else if (path.startsWith("/api/order")     && "POST".equals(method)) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                response = handleOrder(body);
            }
            else if (path.startsWith("/api/inventory") && "GET".equals(method))  response = handleInventory();
            else { response = "{\"error\":\"Unknown endpoint\"}"; status = 404; }
        } catch (Exception e) {
            response = "{\"error\":\"" + escape(e.getMessage()) + "\"}";
            status = 500;
        }

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    // ── /api/menu ──────────────────────────────────────────────────────────
    private String handleMenu() {
        KioskMachine machine = KioskMachine.getInstance();
        Map<String, FlavorStock> stocks = machine.getInventory().getAllStocks();
        String todayFlavor = machine.getTodayRotationFlavor();
        String[] order = {"Yogurt", "Milk", "DarkChocolate", "TwoTone"};

        StringBuilder sb = new StringBuilder("{\"todayFlavor\":\"").append(todayFlavor).append("\",\"flavors\":[");
        boolean first = true;
        for (String flavor : order) {
            FlavorStock s = stocks.get(flavor);
            if (s == null) continue;
            if (!first) sb.append(",");
            boolean availToday = machine.isFlavorAvailableToday(flavor);
            String status = !availToday    ? "UNAVAILABLE"
                          : s.isOutOfStock() ? "SOLD_OUT"
                          : s.isLowStock()   ? "LOW_STOCK"
                          : "AVAILABLE";
            sb.append("{\"name\":\"").append(flavor)
              .append("\",\"stock\":").append(s.getQuantity())
              .append(",\"status\":\"").append(status)
              .append("\",\"availableToday\":").append(availToday).append("}");
            first = false;
        }
        sb.append("]}");
        return sb.toString();
    }

    // ── /api/price ─────────────────────────────────────────────────────────
    private String handlePrice(String query) {
        Map<String, String> p = parseQuery(query);
        String flavor      = p.getOrDefault("flavor", "Yogurt");
        String size        = p.getOrDefault("size", "small");
        String toppingsStr = p.getOrDefault("toppings", "");

        IceCream ice = buildIceCream(flavor, size, toppingsStr);
        return String.format("{\"description\":\"%s\",\"price\":%.2f}",
                escape(ice.getDescription()), ice.getPrice());
    }

    // ── /api/order ─────────────────────────────────────────────────────────
    private String handleOrder(String body) {
        Map<String, String> p = parseJson(body);
        String flavor        = p.getOrDefault("flavor", "Yogurt");
        String size          = p.getOrDefault("size", "small");
        String toppingsStr   = p.getOrDefault("toppings", "");
        String paymentMethod = p.getOrDefault("paymentMethod", "cash");
        double cashAmount    = parseDouble(p.getOrDefault("cashAmount", "0"));
        String phone         = p.getOrDefault("phone", "0xx-xxx-xxxx");

        KioskMachine machine = KioskMachine.getInstance();

        if (!machine.isFlavorAvailableToday(flavor)) {
            return "{\"success\":false,\"message\":\"" + flavor + " is not available today.\"}";
        }
        if (!machine.getInventory().isAvailable(flavor)) {
            return "{\"success\":false,\"message\":\"" + flavor + " is sold out.\"}";
        }

        IceCream ice   = buildIceCream(flavor, size, toppingsStr);
        double   total = ice.getPrice();

        if ("cash".equalsIgnoreCase(paymentMethod) && cashAmount < total) {
            return String.format("{\"success\":false,\"message\":\"Insufficient cash. Need ฿%.2f more.\"}", total - cashAmount);
        }

        PaymentStrategy payment = "qr".equalsIgnoreCase(paymentMethod)
                ? new QRPaymentStrategy(phone)
                : new CashPaymentStrategy(cashAmount);

        Order order = machine.placeOrder(ice, payment, flavor);
        if (order != null && "COMPLETED".equals(order.getStatus())) {
            double change = "cash".equalsIgnoreCase(paymentMethod) ? Math.max(0, cashAmount - total) : 0;
            String ref    = "qr".equalsIgnoreCase(paymentMethod)
                    ? UUID.randomUUID().toString().substring(0, 8).toUpperCase() : "";
            return String.format(
                "{\"success\":true,\"orderId\":%d,\"description\":\"%s\",\"total\":%.2f," +
                "\"change\":%.2f,\"paymentMethod\":\"%s\",\"ref\":\"%s\"}",
                order.getOrderId(), escape(ice.getDescription()), total, change, paymentMethod, ref);
        }
        return "{\"success\":false,\"message\":\"Order could not be processed.\"}";
    }

    // ── /api/inventory ─────────────────────────────────────────────────────
    private String handleInventory() {
        KioskMachine machine = KioskMachine.getInstance();
        Map<String, FlavorStock> stocks = machine.getInventory().getAllStocks();
        String[] order = {"Yogurt", "Milk", "DarkChocolate", "TwoTone"};

        StringBuilder sb = new StringBuilder("{\"inventory\":[");
        boolean first = true;
        for (String flavor : order) {
            FlavorStock s = stocks.get(flavor);
            if (s == null) continue;
            if (!first) sb.append(",");
            String status = s.isOutOfStock() ? "SOLD_OUT"
                          : s.isLowStock()   ? "LOW_STOCK" : "AVAILABLE";
            sb.append("{\"name\":\"").append(flavor)
              .append("\",\"stock\":").append(s.getQuantity())
              .append(",\"status\":\"").append(status).append("\"}");
            first = false;
        }
        sb.append("]}");
        return sb.toString();
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    /**
     * Builds a Decorator chain for the given flavor, size, and topping list.
     * For Large orders the first two toppings are applied with price=0 (free).
     */
    private IceCream buildIceCream(String flavor, String size, String toppingsStr) {
        IceCream ice = new BaseIceCream(flavor, size);
        if (toppingsStr == null || toppingsStr.isEmpty()) return ice;

        int freeAllowance = "large".equalsIgnoreCase(size) ? 2 : 0;
        int freeUsed = 0;

        for (String id : toppingsStr.split(",")) {
            id = id.trim();
            if (id.isEmpty()) continue;
            String displayName = TOPPING_DISPLAY.getOrDefault(id, id);
            double basePrice   = TOPPING_PRICES.getOrDefault(id, 5.0);
            double price       = (freeUsed < freeAllowance) ? 0.0 : basePrice;
            if (freeUsed < freeAllowance) freeUsed++;
            ice = new ToppingDecorator(ice, displayName, price);
        }
        return ice;
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new LinkedHashMap<>();
        if (query == null || query.isEmpty()) return map;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2)
                map.put(kv[0], URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
        }
        return map;
    }

    private Map<String, String> parseJson(String json) {
        Map<String, String> map = new LinkedHashMap<>();
        if (json == null) return map;
        json = json.trim().replaceAll("^\\{|\\}$", "");
        for (String pair : json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) {
                String key = kv[0].trim().replaceAll("\"", "");
                String val = kv[1].trim().replaceAll("^\"|\"$", "");
                map.put(key, val);
            }
        }
        return map;
    }

    private double parseDouble(String s) {
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { return 0; }
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
