package examples;

import algorithm.Modes;
import algorithm.MusicCGP;
import algorithm.MusicCircuit;
import com.jsyn.JSyn;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.PowerOfTwo;
import com.jsyn.unitgen.SineOscillator;
import modules.UnitConstant;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static void test00() {
        final var synth = JSyn.createSynthesizer();
        final var lineOut = new LineOut();
        synth.add(lineOut);
        final var c = new UnitConstant(1000);
        synth.add(c);
        final var sine = new SineOscillator();
        synth.add(sine);
        c.output.connect(sine.frequency);
        sine.output.connect(lineOut);

        synth.start();
        lineOut.start();
        new Scanner(System.in).nextLine();
        synth.stop();
    }

    @Test
    public void testMain() {
        final MusicCircuit circuit = new MusicCircuit(10, 2, 10, 2);
        final MusicCGP cgp = new MusicCGP(circuit, 4, 2.0);
        cgp.setListeningTime(1.2);
        cgp.evolve();
    }

    @Test
    public void testOverflow() {
        final var synth = JSyn.createSynthesizer();
        final var out = new LineOut();
        synth.add(out);

        final var fifty = new UnitConstant(50);
        synth.add(fifty);
        final var m3 = new PowerOfTwo();
        synth.add(m3);
        fifty.output.connect(m3.input);

        final var m8 = new PowerOfTwo();
        synth.add(m8);
//        m3.output.connect(m8.input);
        m8.input.set(20000000000000000D);
        m8.output.connect(out);

        synth.start();
        out.start();
        try {
            Thread.sleep(1000);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        synth.stop();
    }

    public static void pickUpComp(final String fileName) {
        Modes.readCompositionAndContinue(new File(fileName));
    }

    @Test
    public void p1() {
        Modes.readCompositionAndContinue(new File("/home/sancho20021/Documents/staz/impro-evolution/ImproEvolution/data/1626366840964/long"));
    }

    @Test
    public void p2() {
        pickUpComp("/home/sancho20021/Documents/staz/impro-evolution/ImproEvolution/data/1626443485409/long");
    }

    private static void playFor2Secs(final String fileName) {
        Modes.readCompositionAndPlay(new File(fileName), 2);
    }

    @Test
    public void goodLocalMutation() {
        playFor2Secs("/home/sancho20021/Documents/staz/impro-evolution/ImproEvolution/data/1626460778246/long");
    }

    @Test
    public void playComp() {
        playFor2Secs("/home/sancho20021/Documents/staz/impro-evolution/ImproEvolution/data/1626450760452/long");
    }

    @Test
    public void playMeow() {
       playFor2Secs("/home/sancho20021/Documents/staz/impro-evolution/ImproEvolution/data/meow/long");
    }

    public static void main(String[] args) {
//        test01();
        new Main().testMain();
    }
}
