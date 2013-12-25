package de.codecentric.xd.ml.unsupervised;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.integration.core.MessageSelector;
import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import de.codecentric.xd.LogEntry;
import de.codecentric.xd.model.DistributionModel;
import de.codecentric.xd.model.SimpleManipulationModel;

public class LogEntryElkiLOFMessageSelectorIntegrationTest {

	private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test() throws Exception {
        ElkiLOFMessageSelector messageSelector = new ElkiLOFMessageSelector(3);
        JsonElkiLOFMessageTransformer transformer = new JsonElkiLOFMessageTransformer(new String[]{"url","httpMethod","duration"});
        LogEntryCreator[] logEntryCreators = new LogEntryCreator[10];
        logEntryCreators[0] = new LogEntryCreator("/home", 50, "GET");
        logEntryCreators[1] = new LogEntryCreator("/partner", 150, "GET");
        logEntryCreators[2] = new LogEntryCreator("/partner", 500, "GET");
        logEntryCreators[3] = new LogEntryCreator("/partner", 200, "POST");
        logEntryCreators[4] = new LogEntryCreator("/movie", 250, "GET");
        logEntryCreators[5] = new LogEntryCreator("/movie", 50, "GET");
        logEntryCreators[6] = new LogEntryCreator("/movie", 300, "POST");
        logEntryCreators[7] = new LogEntryCreator("/actor", 100, "GET");
        logEntryCreators[8] = new LogEntryCreator("/actor", 300, "POST");
        logEntryCreators[9] = new LogEntryCreator("/actor", 20, "DELETE");
        Random random = new Random();
        for (int i = 0; i<10;i++){
        	int acceptCount = 0;
	        for (int j = 0; j<1000;j++){
	        	LogEntry logEntry = logEntryCreators[random.nextInt(10)].createLogEntry();
	        	String jsonMessageText = mapper.writeValueAsString(logEntry);
	    		Message<?> transformedMessage = transformer.transform(new GenericMessage<String>(jsonMessageText));
	    		if (messageSelector.accept(transformedMessage)){
	    			acceptCount++;
	    		}
	        }
	        System.out.println(new Date()+"; i = "+i+"; Number of unfiltered messages: "+acceptCount);
        }
        LogEntryCreator[] logEntryCreatorsWithoutVariance = new LogEntryCreator[10];
        logEntryCreatorsWithoutVariance[0] = new LogEntryCreator("/home", 50, "GET",0);
        logEntryCreatorsWithoutVariance[1] = new LogEntryCreator("/partner", 150, "GET",0);
        logEntryCreatorsWithoutVariance[2] = new LogEntryCreator("/partner", 500, "GET",0);
        logEntryCreatorsWithoutVariance[3] = new LogEntryCreator("/partner", 200, "POST",0);
        logEntryCreatorsWithoutVariance[4] = new LogEntryCreator("/movie", 250, "GET",0);
        logEntryCreatorsWithoutVariance[5] = new LogEntryCreator("/movie", 50, "GET",0);
        logEntryCreatorsWithoutVariance[6] = new LogEntryCreator("/movie", 300, "POST",0);
        logEntryCreatorsWithoutVariance[7] = new LogEntryCreator("/actor", 100, "GET",0);
        logEntryCreatorsWithoutVariance[8] = new LogEntryCreator("/actor", 300, "POST",0);
        logEntryCreatorsWithoutVariance[9] = new LogEntryCreator("/actor", 20, "DELETE",0);
        for (int i = 0;i<10;i++){
        	LogEntry logEntry = logEntryCreatorsWithoutVariance[i].createLogEntry();
        	String jsonMessageText = mapper.writeValueAsString(logEntry);
            assertThat(messageSelector.accept(transformer.transform(new GenericMessage<String>(jsonMessageText))),is(false));
        }
		assertLogEntryAsUnfiltered(messageSelector, transformer, "/partner", 350, "GET");
		assertLogEntryAsUnfiltered(messageSelector, transformer, "/partner", 500, "POST");
		assertLogEntryAsUnfiltered(messageSelector, transformer, "/partner", 3500, "GET");
		assertLogEntryAsUnfiltered(messageSelector, transformer, "/actor", 200, "GET");
		assertLogEntryAsUnfiltered(messageSelector, transformer, "/actor", 3000, "GET");
		assertLogEntryAsUnfiltered(messageSelector, transformer, "/movie", 400, "GET");
		assertLogEntryAsUnfiltered(messageSelector, transformer, "/actor", 300, "GET");
		assertLogEntryAsUnfiltered(messageSelector, transformer, "/actor", 300, "DELETE");
    }

	private void assertLogEntryAsUnfiltered(MessageSelector messageSelector, Transformer transformer, String url, int baseDuration, String httpMethod) throws IOException,
			JsonGenerationException, JsonMappingException {
		LogEntryCreator logEntryCreator = new LogEntryCreator(url, baseDuration, httpMethod);
    	LogEntry logEntry = logEntryCreator.createLogEntry();
    	String jsonMessageText = mapper.writeValueAsString(logEntry);
        assertThat(messageSelector.accept(transformer.transform(new GenericMessage<String>(jsonMessageText))),is(true));
	}

    private static final class LogEntryCreator{
    	
    	private String url;
    	private String httpMethod;
    	private DistributionModel durationModel;
		
    	public LogEntryCreator(String url, int baseDuration, String httpMethod) {
    		this(url, baseDuration, httpMethod, 10);
    	}

    	public LogEntryCreator(String url, int baseDuration, String httpMethod, double variance) {
			super();
			this.url = url;
			this.httpMethod = httpMethod;
			this.durationModel = new DistributionModel(new SimpleManipulationModel(1), baseDuration, variance);
		}
    	
    	public LogEntry createLogEntry(){
    		return new LogEntry(new Date(),"host","application",url,httpMethod,200,durationModel.getValue(System.currentTimeMillis()));
    	}
    	
    }

}
