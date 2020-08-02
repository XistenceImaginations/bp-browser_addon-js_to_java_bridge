package xi.lib.server;


import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Logger-class for the {@link AppServer}.
 *
 * @author K. P. Uecker (aka xistence imagination)
 * @email k_p.uecker@xistenceimaginations.de
 */
public class AppServerLogger {

    public static Logger create(final String id, final String filePath) {
        Logger logger = Logger.getLogger(id);

        try {
            FileHandler fh = new FileHandler(filePath);
            fh.setFormatter(new Formatter() {
                private static final String PATTERN = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

                @Override
                public String format(LogRecord record) {
                    return String.format(PATTERN,
                            new Date(record.getMillis()),
                            record.getLevel().getLocalizedName(),
                            record.getMessage()
                    );
                }
            });

            logger.addHandler(fh);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }

//        logger.setUseParentHandlers(false); // remove console handler
        return logger;
    }

    private AppServerLogger() {
    }
}
