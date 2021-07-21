package examples.manual;

import com.jsyn.unitgen.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FilterState {
    @Test
    public void test01() {
        final var out = new LineOut();

        final var sine1 = new SineOscillator();
        final var sine2 = new SineOscillator();
        final var filter = new FilterStateVariable();
        final var add = new Add();
        sine1.frequency.set(150);
        sine2.frequency.set(900);
        sine1.output.connect(add.inputA);
        sine2.output.connect(add.inputB);
        add.output.connect(filter.input);

//        filter.amplitude.set(0.01);
        filter.highPass.connect(out);
//        add.output.connect(out);

        filter.frequency.set(600);
        Common.playSynth(List.of(sine1, sine2, add, filter), out);
    }
}
