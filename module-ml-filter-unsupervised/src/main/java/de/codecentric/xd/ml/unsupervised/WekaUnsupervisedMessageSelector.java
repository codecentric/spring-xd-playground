package de.codecentric.xd.ml.unsupervised;

import org.springframework.integration.core.MessageSelector;
import org.springframework.messaging.Message;

/**
 * This message selector uses unsupervised classifying of incoming messages. Messages similar to messages that come
 * regularly are discarded, only suspicious, unnormal messages are let through for further investigation.
 * 
 * @author tobias.flohre
 */
public class WekaUnsupervisedMessageSelector implements MessageSelector {

	/* (non-Javadoc)
	 * @see org.springframework.integration.core.MessageSelector#accept(org.springframework.messaging.Message)
	 */
	@Override
	public boolean accept(Message<?> message) {
		// TODO Auto-generated method stub
		return false;
	}

}
