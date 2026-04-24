package machine;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * KioskServer — starts a lightweight HTTP server (Java built-in, no frameworks needed)
 * so the HTML kiosk UI can communicate with the Java backend via REST calls on localhost:8080.
 */
public class KioskServer {

    private static final int PORT = 8080;
    private HttpServer server;

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api", new KioskApiHandler());
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();

        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║  Parabola Kiosk API running on localhost:" + PORT + "    ║");
        System.out.println("║  Open  parabola-kiosk.html  in your browser     ║");
        System.out.println("║  Press Ctrl+C to stop the server                ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("[SERVER]: Parabola Kiosk server stopped.");
        }
    }
}
