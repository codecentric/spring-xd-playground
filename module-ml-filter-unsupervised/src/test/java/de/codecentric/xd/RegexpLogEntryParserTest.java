package de.codecentric.xd;

import org.junit.Test;

import java.text.DateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RegexpLogEntryParserTest {
    @Test
    public void testParseLogLine() throws Exception {
        LogEntry logEntry = new RegexpLogEntryParser(
                RegexpLogEntryParser.APACHE_LOG_FORMAT,
                RegexpLogEntryParser.APACHE_DATE_FORMAT).parseLogLine(
                "62.172.72.131 - - " +
                "[02/Jan/2003:02:06:41 -0700] " +
                "\"GET /foo/bar HTTP/1.0\" " +
                "200 10564 \"-\" " +
                "\"Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 4.0; WWP 17 August 2001)\"");
        assertNotNull(logEntry);
        assertEquals(logEntry.getDate().getTime(), 1041498401000l);
        assertEquals(logEntry.getHttpMethod(), "GET");
        assertEquals(logEntry.getUrl(), "/foo/bar");
        assertEquals(logEntry.getHttpStatusCode(), 200);
        assertEquals(logEntry.getDuration(), 10564);
    }
}
