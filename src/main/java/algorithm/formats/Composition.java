package algorithm.formats;

import algorithm.CircuitInfo;
import algorithm.Genome;

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
}
