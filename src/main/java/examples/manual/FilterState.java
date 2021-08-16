package examples.manual;

import cgpmodules.PitchQuantizer;
import com.jsyn.unitgen.*;
import org.junit.Test;

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

    @Test
    public void test02() {
        final var out = new LineOut();
        final var sine = new SineOscillatorPhaseModulated();
        final var m = new SineOscillator();
        m.output.connect(sine.modulation);

        sine.frequency.set(1000);
        sine.output.connect(out);

        Common.playSynth(List.of(sine, m), out);
    }

    @Test
    public void test03() {
        final var out = new LineOut();
        final var sine = new SineOscillator();
        final var m = new TriangleOscillator();
        m.frequency.set(0.1);
        final var p2f = new FunctionEvaluator();

//        p2f.function.set(p -> 400 + (p + 1) * 200);
        p2f.function.set(p -> 200 + (Math.pow(2, p) - 0.5) * 4 / 3 * 200);  // ????

//        final var q = PitchQuantizer.getChromaticStandard();
        final var q = new PitchQuantizer(new boolean[]{true, false, false, false, true, false, false, true, false, false, false, false});


        m.output.connect(p2f);
        p2f.output.connect(q);
        q.output.connect(sine.frequency);

        sine.output.connect(out);

        Common.playSynth(List.of(sine, m, p2f, q), out);
    }
}
