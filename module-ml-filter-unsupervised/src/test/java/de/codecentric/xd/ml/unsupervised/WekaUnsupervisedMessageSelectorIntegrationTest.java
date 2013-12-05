package de.codecentric.xd.ml.unsupervised;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.messaging.support.GenericMessage;

public class WekaUnsupervisedMessageSelectorIntegrationTest {

	@Test
	public void test() throws Exception {
		WekaUnsupervisedMessageSelector wekaUnsupervisedMessageSelector = new WekaUnsupervisedMessageSelector();
		for (int i  = 0; i<= 100; i++){
			wekaUnsupervisedMessageSelector.accept(new GenericMessage<String>("2"));
		}
		for (int i  = 0; i<= 100; i++){
			wekaUnsupervisedMessageSelector.accept(new GenericMessage<String>("4"));
		}
		for (int i  = 0; i<= 100; i++){
			wekaUnsupervisedMessageSelector.accept(new GenericMessage<String>("7"));
		}
		assertThat(wekaUnsupervisedMessageSelector.accept(new GenericMessage<String>("7")),is(false));
		assertThat(wekaUnsupervisedMessageSelector.accept(new GenericMessage<String>("3")),is(true));
		assertThat(wekaUnsupervisedMessageSelector.accept(new GenericMessage<String>("4")),is(false));
		assertThat(wekaUnsupervisedMessageSelector.accept(new GenericMessage<String>("9")),is(true));
	}

}
