package de.codecentric.xd.ml.unsupervised;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import de.codecentric.xd.LogEntry;

public class JsonElkiLOFMessageTransformerIntegrationTest {

	private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test() throws Exception {
    	String[] fields = {"url","httpMethod","duration"};
		JsonElkiLOFMessageTransformer transformer = new JsonElkiLOFMessageTransformer(fields);
		LogEntry entry = new LogEntry(new Date(),"Host123","application123", "/contacts", 
				"GET", 200, 123, 123);
		String jsonMessageText = mapper.writeValueAsString(entry);
		Message<?> resultMessage = transformer.transform(new GenericMessage<String>(jsonMessageText));
    	
        assertThat(resultMessage.getHeaders().containsKey(ElkiLOFMessageSelector.ELKI_DOUBLE_VECTOR),is(true));
        Double[] vector = (Double[]) resultMessage.getHeaders().get(ElkiLOFMessageSelector.ELKI_DOUBLE_VECTOR);
        assertThat(vector.length,is(3));
        assertThat(vector[0],is((double)"/contacts".hashCode()));
        assertThat(vector[1],is((double)"GET".hashCode()));
        assertThat(vector[2],is((double)123));
    }

}
