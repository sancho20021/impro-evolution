package examples.manual;

import com.jsyn.JSyn;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.UnitGenerator;

import java.util.List;
import java.util.Scanner;

public class Common {
    public static void playSynth(final List<UnitGenerator> units, final LineOut lineOut) {
        final var synth = JSyn.createSynthesizer();
        synth.start();
        for (final var unit : units) {
            synth.add(unit);
        }
        synth.add(lineOut);

        lineOut.start();
        new Scanner(System.in).nextLine();
    }
}
