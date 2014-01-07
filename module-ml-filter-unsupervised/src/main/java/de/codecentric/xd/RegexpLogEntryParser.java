package de.codecentric.xd;

import de.codecentric.xd.LogEntry;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpLogEntryParser implements LogEntryParser {

    public static final Pattern APACHE_LOG_FORMAT = Pattern.compile(
            "(?<ip>(?:\\d+\\.){3}\\d+)\\s+.*" +
                    "\\[(?<date>.*)\\]\\s+" +
                    "\"(?<method>\\S+)\\s+(?<path>\\S+)\\s+.*\"\\s+" +
                    "(?<code>\\d+)\\s+(?<duration>\\d+)\\s+" +
                    "\"(?<referrer>[^\"]+)\"\\s+" +
                    "\"(?<agent>[^\"]+)\"");
    public static final String APACHE_DATE_FORMAT = "dd/MMM/yyyy:hh:mm:ss Z";

    private final Pattern logFormat;
    private final String dateFormat;

    public RegexpLogEntryParser(Pattern logFormat, String dateFormat) {
        this.logFormat = logFormat;
        this.dateFormat = dateFormat;
    }

    @Override
    public LogEntry parseLogLine(String logline) {
        Matcher matcher = logFormat.matcher(logline);

        if (!matcher.matches())
            return null;

        try {
            return new LogEntry(
                    new SimpleDateFormat(dateFormat).parse(matcher.group("date")),
                    "host",
                    "application",
                    matcher.group("path"),
                    matcher.group("method"),
                    Integer.parseInt(matcher.group("code")),
                    Integer.parseInt(matcher.group("duration")));
        } catch (ParseException e) {
            return null;
        }


    }

}
