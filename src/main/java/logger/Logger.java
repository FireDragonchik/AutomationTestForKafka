package logger;

public final class Logger {

    private static final ThreadLocal<org.apache.log4j.Logger> log4J = ThreadLocal.withInitial(()
            -> org.apache.log4j.Logger.getLogger(String.valueOf(Thread.currentThread().getId())));
    private static final ThreadLocal<Logger> instance = ThreadLocal.withInitial(Logger::new);

    private Logger() {
    }

    public static Logger getInstance() {
        return instance.get();
    }

    public void info(String message) {
        log4J.get().info(message);
    }

    public void info(String message, Object... keys) {
        info(String.format(message, keys));
    }

    public void error(String message, Exception e) {
        log4J.get().error(message);
        log4J.get().error(e.fillInStackTrace());
    }
}
