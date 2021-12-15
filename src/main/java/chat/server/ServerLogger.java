package chat.server;

import homework.HW_36;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLogger {
    public static void log(String s) {
        Logger logger = LogManager.getLogger(ServerLogger.class);
        logger.trace(s);
    }
}
