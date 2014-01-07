package de.codecentric.xd.ml.unsupervised;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;

import java.io.IOException;

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
                if (node == null) {
                    return message;
                }

                for (int i = 0; i < fields.length; i++) {
                    JsonNode fieldNode = node.get(fields[i]);
                    if (fieldNode == null) {
                        return message;
                    }
                    vector[i] = fieldNode.asDouble(fieldNode.asText().hashCode());
                }
                return MessageBuilder.fromMessage(message).setHeader(ElkiLOFMessageSelector.ELKI_DOUBLE_VECTOR, vector).build();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Only JSON-Strings are allowed as message content.", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception in JsonElkiLOFMessageTransformer", e);
        }
        throw new IllegalArgumentException(
                "Only JSON-Strings are allowed as message content.");
    }

}
