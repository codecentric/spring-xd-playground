package de.codecentric.xd.ml.unsupervised;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.messaging.support.GenericMessage;

import java.util.Random;

public class ElkiLOFMessageSelectorIntegrationTest {

    private Random random = new Random();

    @Test
    public void test() throws Exception {
        ElkiLOFMessageSelector wekaUnsupervisedMessageSelector = new ElkiLOFMessageSelector();
        for (int i  = 0; i<= 100; i++){
            wekaUnsupervisedMessageSelector.accept(new GenericMessage<Double[]>(randomize(1d,1d,0.2)));
        }
        for (int i  = 0; i<= 100; i++){
            wekaUnsupervisedMessageSelector.accept(new GenericMessage<Double[]>(randomize(3d,1d,0.2)));
        }
        for (int i  = 0; i<= 100; i++){
            wekaUnsupervisedMessageSelector.accept(new GenericMessage<Double[]>(randomize(2d,5d,0.2)));
        }
        assertThat(wekaUnsupervisedMessageSelector.accept(new GenericMessage<Double[]>(new Double[] {1d,1d})),is(false));
        assertThat(wekaUnsupervisedMessageSelector.accept(new GenericMessage<Double[]>(new Double[] {1d,3d})),is(true));
        assertThat(wekaUnsupervisedMessageSelector.accept(new GenericMessage<Double[]>(new Double[] {2d,5d})),is(false));
        assertThat(wekaUnsupervisedMessageSelector.accept(new GenericMessage<Double[]>(new Double[] {7d,1d})),is(true));
    }

    private Double[] randomize(double x, double y, double r) {
        return new Double[] {x + (random.nextDouble()-0.5)*2*r, y+(random.nextDouble() -0.5)*2*r};
    }

}
