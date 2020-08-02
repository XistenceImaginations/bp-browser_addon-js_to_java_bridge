package xi.lib.server;

/**
 * Wrapper class to form a response for a route.
 *
 * @author K. P. Uecker (aka xistence imagination)
 * @email k_p.uecker@xistenceimaginations.de
 */
public class AppResponse {

    public static AppResponse response(final int status, final String type, final String body) {
        return new AppResponse(status, type, body);
    }

    public final int status;
    public final String body, type;

    private AppResponse(final int status, final String type, final String body) {
        this.status = status;
        this.type = type;
        this.body = body;
    }
}
