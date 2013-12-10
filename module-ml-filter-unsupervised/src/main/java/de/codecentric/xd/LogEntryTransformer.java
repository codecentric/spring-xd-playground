package de.codecentric.xd;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class LogEntryTransformer {

	private ObjectMapper mapper = new ObjectMapper();

	public String transform(String payload) throws JsonGenerationException, JsonMappingException, IOException {
		LogEntry logEntry = new LogEntry(payload);
		return mapper.writeValueAsString(logEntry);
	}
}
