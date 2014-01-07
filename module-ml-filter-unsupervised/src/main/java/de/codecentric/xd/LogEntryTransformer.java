package de.codecentric.xd;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class LogEntryTransformer {

    private final ObjectMapper mapper = new ObjectMapper();
    private final LogEntryParser logEntryParser;

    public LogEntryTransformer(String logLineParser) {
        switch (logLineParser) {
            case "APACHE":
                logEntryParser = new RegexpLogEntryParser(
                        RegexpLogEntryParser.APACHE_LOG_FORMAT,
                        RegexpLogEntryParser.APACHE_DATE_FORMAT);
                break;
            case "DEFAULT":
            default:
                logEntryParser = new DefaultLogEntryParser();
        }

    }

    public String transform(String payload) throws JsonGenerationException, JsonMappingException, IOException {
        LogEntry logEntry = logEntryParser.parseLogLine(payload);
        return mapper.writeValueAsString(logEntry);
    }
}
