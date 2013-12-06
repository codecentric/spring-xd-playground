package de.codecentric.xd.ml.unsupervised;

import org.junit.Test;
import org.springframework.messaging.support.GenericMessage;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ElkiLOFMessageSelectorIntegrationTest {

    @Test
    public void test() throws Exception {
        ElkiLOFMessageSelector elkiLOFMessageSelector = new ElkiLOFMessageSelector();
        for (int i = 0; i <= 100; i++) {
            elkiLOFMessageSelector.accept(new GenericMessage<String>("2"));
        }
        for (int i = 0; i <= 100; i++) {
            elkiLOFMessageSelector.accept(new GenericMessage<String>("4"));
        }
        for (int i = 0; i <= 100; i++) {
            elkiLOFMessageSelector.accept(new GenericMessage<String>("7"));
        }
        assertThat(elkiLOFMessageSelector.accept(new GenericMessage<String>("7")), is(false));
        assertThat(elkiLOFMessageSelector.accept(new GenericMessage<String>("3")), is(true));
        assertThat(elkiLOFMessageSelector.accept(new GenericMessage<String>("4")), is(false));
        assertThat(elkiLOFMessageSelector.accept(new GenericMessage<String>("9")), is(true));
    }

}
