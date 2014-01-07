package de.codecentric.xd.ml.unsupervised;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import java.util.Random;

public class ElkiLOFMessageSelectorIntegrationTest {

    private Random random = new Random();

    private ElkiLOFMessageSelector selector;
    private DefaultElkiLOFMessageTransformer transfomer;

    @Before
    public void setUp() throws Exception {
        selector = new ElkiLOFMessageSelector(2);
        transfomer = new DefaultElkiLOFMessageTransformer();
    }

    @Test
    public void test() throws Exception {
        for (int i  = 0; i<= 100; i++){
            selector.accept(buildTransformedMessage(randomize(1d, 1d, 0.2)));
        }
        for (int i  = 0; i<= 100; i++){
            selector.accept(buildTransformedMessage(randomize(3d, 1d, 0.2)));
        }
        for (int i  = 0; i<= 100; i++){
            selector.accept(buildTransformedMessage(randomize(2d, 5d, 0.2)));
        }
        assertThat(selector.accept(buildTransformedMessage(new Double[]{1d, 1d})),is(false));
        assertThat(selector.accept(buildTransformedMessage(new Double[]{1d, 3d})),is(true));
        assertThat(selector.accept(buildTransformedMessage(new Double[]{2d, 5d})),is(false));
        assertThat(selector.accept(buildTransformedMessage(new Double[]{7d, 1d})),is(true));
    }

    private Message<?> buildTransformedMessage(Double[] payload) {
        return transfomer.transform(new GenericMessage<Double[]>(payload));
    }

    private Double[] randomize(double x, double y, double r) {
        return new Double[] {x + (random.nextDouble()-0.5)*2*r, y+(random.nextDouble() -0.5)*2*r};
    }

}
