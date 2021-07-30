package files.formats;

import algorithm.CircuitInfo;
import algorithm.Genome;

import java.io.File;
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

    public void saveToWav(final File wavFile, final double secondsPerGenome) {
        if (secondsPerGenome <= 0) {
            throw new IllegalArgumentException("secondsPerGenome must be > 0");
        }
        // TODO
    }
}
