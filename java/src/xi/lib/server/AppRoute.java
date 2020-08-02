package xi.lib.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * Class to define a route. Use
 * {@link AppRoute#route(java.util.function.Function)} to set up a definition by
 * providing a {@link Function} handling the input and providing an output with
 * an instance of {@link AppResponse}.
 *
 * @author K. P. Uecker (aka xistence imagination)
 * @email k_p.uecker@xistenceimaginations.de
 */
public class AppRoute {

    public static HttpHandler route(final Function<String, AppResponse> fnc) {
        return exchange -> new AppRoute(fnc).receive(exchange);
    }

    private AppRoute receive(final HttpExchange exchange) {
        System.out.println(exchange.getRequestURI());
        System.out.println(exchange.getRequestMethod());

        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), "UTF-8")) {
            final StringBuilder body = new StringBuilder();
            final char[] buffer = new char[256];

            int read;

            while ((read = reader.read(buffer)) != -1) {
                body.append(buffer, 0, read);
            }

            AppServer.DEBUG.log(Level.INFO, "\tReceived data: {0}", body);

            switch (exchange.getRequestMethod().toUpperCase()) {
                case "OPTIONS":
                    this.send(exchange, 200, "application/json", ""); // check for preflight (CORS-Check, happens before POST and must be successful for a valid POST-handling)
                    break;
                case "POST":
                    if (this.fnc != null) {
                        AppResponse response = this.fnc.apply(body.toString());

                        if (response != null) {
                            this.send(exchange, response.status, response.type, response.body);
                        } else {
                            this.send(exchange, 404, "application/json", "{ \"message\": \"This route or method is not supported\" }");
                        }
                    } else {
                        this.send(exchange, 500, "application/json", "{ \"message\": \"Server-error: missing handler\" }");
                    }
                    break;
            }
        } catch (IOException ex) {
            AppServer.ERROR.throwing(AppServer.class.getSimpleName(), "receive(HttpExchange)", ex);
        }

        return this;
    }

    private AppRoute send(final HttpExchange exchange, final int status, final String type, final String body) {
        try {
            final Headers //
                    requestHeaders = exchange.getRequestHeaders(),
                    responseHeaders = exchange.getResponseHeaders();

            requestHeaders
                    .entrySet()
                    .forEach(entry -> responseHeaders.put(entry.getKey(), entry.getValue()));
            responseHeaders.put("Content-Type", Arrays.asList(type));
            responseHeaders.put("Access-Control-Allow-Headers", Arrays.asList("X-PINGOTHER", "Content-Type"));
            responseHeaders.put("Access-Control-Allow-Origin", requestHeaders.keySet().stream()
                    .filter(key -> "origin".equalsIgnoreCase(key))
                    .map(key -> (List<String>) requestHeaders.get(key))
                    .findFirst().orElse(null));

            if (body != null) {
                exchange.sendResponseHeaders(status, body.length() == 0 ? -1 : body.length());

                if (body.length() > 0) {
                    try (OutputStream out = exchange.getResponseBody()) {
                        out.write(body.getBytes("UTF-8"));
                    }
                }
            }

            exchange.close();
        } catch (IOException ex) {
            AppServer.ERROR.throwing(AppServer.class.getSimpleName(), "send(HttpExchange,String)", ex);
        }

        return this;
    }

    private final Function<String, AppResponse> fnc;

    private AppRoute(final Function<String, AppResponse> fnc) {
        this.fnc = fnc;
    }
}
