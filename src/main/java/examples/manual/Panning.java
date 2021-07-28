package examples.manual;

import com.jsyn.unitgen.*;
import org.junit.Test;

import java.util.List;

public class Panning {
    @Test
    public void test01() {
        final var sine = new SineOscillator();
        final var saw = new SawtoothOscillator();
        final var out = new LineOut();

        final var panL = new Pan();
        panL.pan.set(-1);
        sine.output.connect(panL.input);

        final var panR = new Pan();
        panR.pan.set(1);
        saw.output.connect(panR.input);

        panL.output.connect(0, out.input, 0);
        panL.output.connect(1, out.input, 1);
        panR.output.connect(0, out.input, 0);
        panR.output.connect(1, out.input, 1);
        Common.playSynth(List.of(sine, saw, panL, panR), out);
    }
}
