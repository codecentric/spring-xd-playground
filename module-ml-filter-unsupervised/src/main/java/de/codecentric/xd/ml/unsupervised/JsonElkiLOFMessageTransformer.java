package de.codecentric.xd.ml.unsupervised;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;

import java.io.IOException;

public class JsonElkiLOFMessageTransformer implements Transformer {

    private static final Logger LOGGER = Logger.getLogger(JsonElkiLOFMessageTransformer.class);

    private ObjectMapper mapper = new ObjectMapper();
    private String[] fields;

    public JsonElkiLOFMessageTransformer(String[] fields) {
        super();
        this.fields = fields;
    }

    @Override
    public Message<?> transform(Message<?> message) {
        if (message.getPayload() instanceof String) {
            JsonNode node = parseJson((String) message.getPayload());

            Double[] vector = new Double[fields.length];
            for (int i = 0; i < fields.length; i++) {
                JsonNode fieldNode = node.get(fields[i]);
                if (fieldNode != null) {
                    vector[i] = fieldNode.asDouble(fieldNode.asText().hashCode());
                } else {
                    LOGGER.warn("Field " + fields[i] + " not present in json");
                    vector[i] = 0d;
                }
            }

            return MessageBuilder.fromMessage(message).setHeader(ElkiLOFMessageSelector.ELKI_DOUBLE_VECTOR, vector).build();
        }

        LOGGER.error("Only JSON-Strings are allowed as message content.");
        throw new IllegalArgumentException("Only JSON-Strings are allowed as message content.");
    }

    private JsonNode parseJson(String payload) {
        JsonNode node;
        try {
            node = mapper.readTree(payload);
        } catch (IOException e) {
            LOGGER.error("Unable to parse JSON from message payload: " + e.getMessage());
            throw new IllegalArgumentException(
                    "Only JSON-Strings are allowed as message content.", e);
        }
        if (node == null) {
            LOGGER.warn("Unable to parse JSON from message payload");
            throw new IllegalArgumentException(
                    "Unable to parse JSON from message payload.");
        }
        return node;
    }

}
