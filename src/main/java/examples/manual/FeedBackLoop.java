package examples.manual;

import com.jsyn.unitgen.*;
import cgpmodules.UnitConstant;
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

    @Test
    public void test02() {
        final var triangle = new TriangleOscillator();
        final var out = new LineOut();
        triangle.output.connect(out);
        final var mul = new Multiply();
        mul.inputA.set(300);
        triangle.output.connect(mul.inputB);

        final var latch = new Latch();
        mul.output.connect(latch.input);
        final var lfo = new SawtoothOscillator();
        lfo.frequency.set(4);
        lfo.output.connect(latch.gate);
        latch.output.connect(triangle.frequency);

        final var c = new UnitConstant(400);
        c.output.connect(triangle.frequency);
        Common.playSynth(List.of(triangle, mul, c, latch, lfo), out);
    }
}
