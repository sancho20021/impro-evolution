package examples.manual;

import com.jsyn.JSyn;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.SquareOscillator;
import modules.UnitConstant;
import org.junit.Test;

import java.util.Scanner;

public class ZeroFreqAndAmp {
    @Test
    public void test01() {
        final var synth = JSyn.createSynthesizer();
        synth.start();

        final var osc = new SquareOscillator();
        synth.add(osc);
        final var zero = new UnitConstant(0);
        synth.add(zero);
//        zero.output.connect(osc.frequency);
        zero.output.connect(osc.amplitude);

        final var out = new LineOut();
        synth.add(out);
        osc.output.connect(out);
        out.start();

        new Scanner(System.in).nextLine();
    }
}
