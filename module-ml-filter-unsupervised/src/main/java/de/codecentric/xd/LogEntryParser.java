package de.codecentric.xd;


public interface LogEntryParser {
    LogEntry parseLogLine(String logline);
}
