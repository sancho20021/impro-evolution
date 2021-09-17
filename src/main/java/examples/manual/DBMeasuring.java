package examples.manual;

import algorithm.MusicCircuit;
import cgpmodules.UnitConstant;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.*;
import com.jsyn.util.WaveRecorder;
import files.Utils;
import files.formats.Composition;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static examples.SaveOneMelodyWav.exportMelodyToWav;

public class DBMeasuring {
    @Test
    public void test01() throws Exception {
        final var synth = JSyn.createSynthesizer();
        final var sine = new SineOscillator();
        sine.amplitude.set(0.5);
        final var add = new Add();
        add.inputA.set(0.5);
        sine.output.connect(add.inputB);
        final var out = new LineOut();
        add.output.connect(out);
        synth.add(sine);
        synth.add(add);
        synth.add(out);
        final File f1 = new File("f1.wav");


        f(synth, add.output, f1);
    }

    @Test
    public void test02() throws Exception {
        final var synth = JSyn.createSynthesizer();
        final var sine = new SineOscillator();
        sine.amplitude.set(0.5);
        final var add = new Add();
        add.inputA.set(1);
        sine.output.connect(add.inputB);
        final var out = new LineOut();
        add.output.connect(out);
        synth.add(sine);
        synth.add(add);
        synth.add(out);
        final File f1 = new File("f2.wav");

        f(synth, add.output, f1);
    }

    @Test
    public void test03() throws Exception {
        final var synth = JSyn.createSynthesizer();
        final var sine = new SineOscillator();
        sine.amplitude.set(0.5);
        final var add = new Add();
        add.inputA.set(2);
        sine.output.connect(add.inputB);
        final var out = new LineOut();
        add.output.connect(out);
        synth.add(sine);
        synth.add(add);
        synth.add(out);
        final File f1 = new File("f3.wav");

        f(synth, add.output, f1);
    }

    private void f(final Synthesizer synth, final UnitOutputPort out, final File file) throws InterruptedException, IOException {
        final var recorder = new WaveRecorder(synth, file);
        out.connect(recorder.getInput());
        synth.start();
        recorder.start();
        Thread.sleep(1000);
        recorder.stop();
        synth.stop();
        recorder.close();
    }

    private static void printDB(final List<UnitGenerator> units, final UnitOutputPort out) throws InterruptedException {
        final var synth = JSyn.createSynthesizer();
        synth.setRealTime(false);
        units.forEach(synth::add);
        final var lineOut = new LineOut();
        final var peakFollower = new PeakFollower();
        peakFollower.halfLife.set(0.1);
        synth.add(lineOut);
        synth.add(peakFollower);
        out.connect(lineOut.getInput().getConnectablePart(0));
        out.connect(lineOut.getInput().getConnectablePart(1));
        out.connect(peakFollower.input);
        synth.start();
        lineOut.start();
        peakFollower.start();
        for (int i = 0; i < 20; i++) {
            synth.sleepFor(0.2);
            System.out.println("output: " + peakFollower.output.get() + ", current: " + peakFollower.current.get());
        }
        synth.stop();
    }

    @Test
    public void test04_peaks() throws Exception {
        final var lfo = new SineOscillator();
        lfo.frequency.set(0.4);
        final var param = new FunctionEvaluator();
        param.function.set(sine -> (sine + 1) / 2);
        lfo.output.connect(param.input);
        final var sine = new SineOscillator();
        param.output.connect(sine.amplitude);
        final var add = new Add();
        add.inputB.set(0.5);
        sine.output.connect(add.inputA);
        printDB(List.of(sine, add, lfo, param), add.output);
    }

    @Test
    public void test05_filtered_constant() throws Exception {
        final var synth = JSyn.createSynthesizer();
        final var c = new UnitConstant(0.7);
        final var filter = new FilterHighPass();
        filter.frequency.set(20);
        c.output.connect(filter.input);
        synth.add(c);
        synth.add(filter);
        final File f1 = new File("filtered_constant.wav");

        f(synth, filter.output, f1);
    }

    @Test
    public void test05_filter() throws Exception {
        final var synth = JSyn.createSynthesizer();
        final var sine = new SineOscillator();
        sine.frequency.set(10);
        final var filter = new FilterHighPass();
        filter.frequency.set(20);
        sine.output.connect(filter.input);
        final var filter2 = new FilterHighPass();
        filter2.frequency.set(20);
        filter.output.connect(filter2.input);


        synth.add(sine);
        synth.add(filter);
        synth.add(filter2);
        final File f1 = new File("filtered_sine.wav");

        f(synth, filter2.output, f1);
    }

    @Test
    public void different_waves_1() {
        exportMelodyToWav("small", "silent", 4);
    }

    @Test
    public void different_waves_2() {
        exportMelodyToWav("small", "sounding", 4);
    }

    private static double getAveragePeak(final int intervals, final MusicCircuit circuit, final double seconds) {
        final var synth = JSyn.createSynthesizer();
        synth.setRealTime(false);

        synth.add(circuit);
        final var peakFollower = new PeakFollower();
        synth.add(peakFollower);
        circuit.getOutput().connect(peakFollower.input);

        synth.start(20000);

        final double step = seconds / intervals;
        double averageAmp = 0;
        int n = 0;
        peakFollower.start();
        for (double i = 0; i < seconds; i += step) {
            try {
                synth.sleepFor(step);
            } catch (final InterruptedException e) {
                System.err.println("Music genome loudness check failed: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
            averageAmp += (peakFollower.output.get() - averageAmp) / (n + 1);  // A_{n+1} = A_n + (x_{n+1} - A_n)/(n+1)
            n++;
        }
        synth.stop();
        return averageAmp;
    }

    private static void play(final List<UnitGenerator> units, final UnitOutputPort out, final double secs) {
        final var synth = JSyn.createSynthesizer();
        units.forEach(synth::add);
        final var lineOut = new LineOut();
        out.connect(0, lineOut.getInput(), 0);
        out.connect(0, lineOut.getInput(), 1);
        synth.add(lineOut);
        synth.start();
        lineOut.start();
        try {
            synth.sleepFor(secs);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        synth.stop();
    }

    @Test
    public void measureAmp() throws IOException {
        final var seconds = 2;
        final var comp = Utils.readObject(Utils.getCompositionFile("good_example"), Composition.class);
        for (final var genome : comp.getGenomes()) {
            final double amp = getAveragePeak(10, new MusicCircuit(comp.getCircuitInfo(), genome), 2);
            System.out.println(amp);

            final var c1 = new MusicCircuit(comp.getCircuitInfo(), genome);
            play(List.of(c1), c1.getOutput(), seconds);

            final var c2 = new MusicCircuit(comp.getCircuitInfo(), genome);
            final var div = new Divide();
            c2.getOutput().connect(div.inputA);
            div.inputB.set(amp);
            play(List.of(c2, div), div.output, seconds);
        }
    }
}
