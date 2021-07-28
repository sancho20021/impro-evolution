package examples;

import algorithm.MusicCGP;
import com.jsyn.JSyn;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.SineOscillator;
import org.junit.Test;

import java.util.List;

public class TestClass {
    @Test
    public void test01_play() {
        final var sine = new SineOscillator();
        final var out = new LineOut();
        sine.output.connect(out);
//        MusicCGP.play(List.of(sine), out, 1);
//        MusicCGP.play(List.of(sine), out, 1);
    }
    @Test
    public void test02() {
        final var synth = JSyn.createSynthesizer();
        final var sine = new SineOscillator();
        final var out = new LineOut();
        sine.output.connect(out);
        synth.add(sine);
        synth.add(out);
        synth.start();
        out.start();
        try {
            synth.sleepFor(1);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        synth.stop();

        try {
            Thread.sleep(1000);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        synth.start();
        out.start();
        try {
            synth.sleepFor(1);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        synth.stop();
    }
}
