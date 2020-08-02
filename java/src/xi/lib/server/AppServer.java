package xi.lib.server;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import static xi.lib.server.AppRoute.route;
import static xi.lib.server.AppResponse.response;

/**
 * Simple Application Server used in combination with a browser extension (but
 * not limited to it).
 *
 * @author K. P. Uecker (aka xistence imagination)
 * @email k_p.uecker@xistenceimaginations.de
 */
public class AppServer {

    public static void main(String[] args) {
        new AppServer();
    }

    public static final Logger //
            DEBUG = AppServerLogger.create("debug", "./debug.log"),
            ERROR = AppServerLogger.create("error", "./error.log");

    public AppServer() {
        this(11211);
    }

    public AppServer(final int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            // define a routes
            server.createContext("/test", route(body -> response(200, "application/json", "{ \"test\": \"this is a test reply\" }")));
            server.createContext("/ping", route(body -> response(200, "application/json", "{ \"state\": \"all ok\" }")));
            server.setExecutor(null);
            server.start();

            DEBUG.info("AppServer started @ port " + port);
        } catch (IOException ex) {
            ERROR.throwing(AppServer.class.getSimpleName(), "AppServer()", ex);
            System.exit(-1);
        }
    }
}
