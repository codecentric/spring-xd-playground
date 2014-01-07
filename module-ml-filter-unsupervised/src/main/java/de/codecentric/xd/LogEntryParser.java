package de.codecentric.xd;

/**
 * Created by michael on 06.01.14.
 */
public interface LogEntryParser {
    LogEntry parseLogLine(String logline);
}
