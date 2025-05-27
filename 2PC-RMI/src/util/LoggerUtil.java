package util;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * LoggerUtil provides a utility to configure custom logging behavior for the application.
 * It sets up a console logger with timestamped messages formatted for readability.
 */
public interface LoggerUtil {

  /**
   * Configures the given logger with a custom console handler.
   * The handler formats logs with timestamps and message details.
   * It also disables parent handlers to avoid duplicate logs.
   *
   * @param logger The logger instance to configure.
   */
  static void setupCustomLogger(Logger logger) {
    logger.setUseParentHandlers(false); // Disable default handlers to prevent duplicate logs
    logger.addHandler(getDateHandler()); // Attach a custom date-formatted handler
    System.setOut(new PrintStream(System.out, true)); // Ensure System.out uses auto flush
  }

  /**
   * Creates a ConsoleHandler with a custom log message formatter.
   * The log messages are formatted with:
   * - A timestamp (HH:mm:ss.SSS)
   * - Log level (INFO, WARNING, ERROR, etc.)
   * - Logger name
   * - The actual log message
   *
   * @return A ConsoleHandler with custom formatting.
   */
  private static ConsoleHandler getDateHandler() {
    ConsoleHandler handler = new ConsoleHandler();

    // Define a custom formatter for log messages
    handler.setFormatter(new Formatter() {
      @Override
      public String format(LogRecord record) {
        String timestamp = new SimpleDateFormat("dd MMM yyyy HH:mm:ss.SSS").format(new Date(record.getMillis()));
        return String.format("[%s] [%s] [%s] %s %n",
                timestamp,
                record.getLevel(),
                record.getLoggerName(),
                record.getMessage());
      }
    });

    handler.setLevel(Level.ALL);
    return handler;
  }
}
