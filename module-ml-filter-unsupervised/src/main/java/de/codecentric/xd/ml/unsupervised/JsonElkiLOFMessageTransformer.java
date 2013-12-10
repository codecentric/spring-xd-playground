package de.codecentric.xd.ml.unsupervised;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;

public class JsonElkiLOFMessageTransformer implements Transformer {

	private ObjectMapper mapper = new ObjectMapper();
	private String[] fields;
	
	public JsonElkiLOFMessageTransformer(String[] fields) {
		super();
		this.fields = fields;
	}

	@Override
	public Message<?> transform(Message<?> message) {
		try {
			if (message.getPayload() instanceof String) {
				Double[] vector = new Double[fields.length];
				JsonNode node = mapper.readTree((String) message.getPayload());
				for (int i = 0; i < fields.length; i++) {
					JsonNode fieldNode = node.get(fields[i]);
					vector[i] = fieldNode.asDouble(fieldNode.asText().hashCode());
				}
				return MessageBuilder.fromMessage(message).setHeader(ElkiLOFMessageSelector.ELKI_DOUBLE_VECTOR,	vector).build();
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"Only JSON-Strings are allowed as message content.",e);
		}
		throw new IllegalArgumentException(
				"Only JSON-Strings are allowed as message content.");
	}

}
