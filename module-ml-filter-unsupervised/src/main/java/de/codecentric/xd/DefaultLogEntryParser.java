package de.codecentric.xd;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class DefaultLogEntryParser implements LogEntryParser {

    public LogEntry parseLogLine(String logline) {
        // TODO add error handling
        String[] splittedEntry = logline.split("#");
        Date date;
        try {
            date = DateFormat.getDateTimeInstance().parse(splittedEntry[0]);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Problem with parsing date.",e);
        }
        String host = splittedEntry[1];
        String application = splittedEntry[2];
        String url = splittedEntry[3];
        String httpMethod = splittedEntry[4];
        Integer httpStatusCode = Integer.parseInt(splittedEntry[5]);
        Integer duration = Integer.parseInt(splittedEntry[6]);
        Integer size = Integer.parseInt(splittedEntry[7]);
        return new LogEntry(date, host, application, url, httpMethod, httpStatusCode, duration, size);
    }
}
