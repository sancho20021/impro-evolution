package files.formats;

import algorithm.CircuitInfo;
import algorithm.Genome;
import algorithm.MusicCircuit;
import com.jsyn.JSyn;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.util.WaveRecorder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Composition {
    private final CircuitInfo circuitInfo;
    private final List<Genome> genomes;

    public Composition(final CircuitInfo info, final List<Genome> genomes) {
        this.circuitInfo = info;
        this.genomes = genomes;
    }

    public Composition(final CircuitInfo info) {
        this(info, new ArrayList<>());
    }

    public void addGenome(final Genome genome) {
        genomes.add(genome);
    }

    public List<Genome> getGenomes() {
        return genomes;
    }

    public CircuitInfo getCircuitInfo() {
        return circuitInfo;
    }

    public void saveToWav(final File waveFile, final double secondsPerGenome) throws IOException {
        if (secondsPerGenome <= 0) {
            throw new IllegalArgumentException("secondsPerGenome must be > 0");
        }
        final var synth = JSyn.createSynthesizer();
        synth.setRealTime(true);  // Without realTime set to true, the program keeps running after finishing
        synth.start();

        final var recorder = new WaveRecorder(synth, waveFile);

        for (int i = 0; i < genomes.size(); i++) {
            final var circuit = new MusicCircuit(circuitInfo, genomes.get(i));
//            final var circuit = new SineOscillator();

            synth.add(circuit);
            circuit.getOutput().connect(0, recorder.getInput(), 0);
            circuit.getOutput().connect(0, recorder.getInput(), 1);
            recorder.start();
            try {
                synth.sleepFor(secondsPerGenome);
            } catch (final InterruptedException e) {
                System.err.println("Recording interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
            recorder.stop();
            circuit.getOutput().disconnectAll();
            synth.remove(circuit);
            System.out.println("Genome " + i + " recorded");
        }
        recorder.close();

        synth.stop();
    }
}
