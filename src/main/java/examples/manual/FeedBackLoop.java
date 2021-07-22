package examples.manual;

import com.jsyn.unitgen.*;
import org.junit.Test;

import java.util.List;

public class FeedBackLoop {
    @Test
    public void test01() {
        final var sine = new SineOscillator();
        sine.frequency.set(200);
        final var square = new SquareOscillator();
        final var mul = new Multiply();
        sine.output.connect(mul.inputA);
        square.output.connect(mul.inputB);

        square.frequency.set(3);

        final var out = new LineOut();

        mul.output.connect(mul.inputA);

        final var select = new Select();
        final var saw = new SawtoothOscillator();
        saw.frequency.set(200);
        mul.output.connect(select.select);
        sine.output.connect(select.inputA);
        saw.output.connect(select.inputB);
        select.output.connect(out);

        Common.playSynth(List.of(sine, mul, square, saw, select), out);
    }
}
