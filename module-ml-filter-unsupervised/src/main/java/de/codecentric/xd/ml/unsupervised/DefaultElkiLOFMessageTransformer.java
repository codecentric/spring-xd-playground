package de.codecentric.xd.ml.unsupervised;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;

public class DefaultElkiLOFMessageTransformer implements Transformer {

	@Override
	public Message<?> transform(Message<?> message) {
		Double[] vector;
		if (message.getPayload() instanceof Double[]){
			vector = (Double[]) message.getPayload();
		} else if (message.getPayload() instanceof Double){
			vector = new Double[1];
			vector[0] = (Double) message.getPayload();
		} else {
			vector = new Double[1];
			vector[0] = (double)message.getPayload().hashCode();
		}
		return MessageBuilder.fromMessage(message).setHeader(ElkiLOFMessageSelector.ELKI_DOUBLE_VECTOR, vector).build();
	}

}
