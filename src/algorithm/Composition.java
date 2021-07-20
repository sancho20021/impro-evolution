package algorithm;

import java.util.ArrayList;
import java.util.List;

public class Composition {
    public final int inputsN, rows, columns, l, maxArity;
    private final List<Genome> genomes;

    public Composition(final int inputsN, final int rows, final int columns, final int l, final int maxArity, final List<Genome> genomes) {
        this.inputsN = inputsN;
        this.rows = rows;
        this.columns = columns;
        this.l = l;
        this.maxArity = maxArity;
        this.genomes = genomes;
    }

    public Composition(final int inputsN, final int rows, final int columns, final int l, final int maxArity) {
        this(inputsN, rows, columns, l, maxArity, new ArrayList<>());
    }

    public void addGenome(final Genome genome) {
        genomes.add(genome);
    }

    public List<Genome> getGenomes() {
        return genomes;
    }

    public Formats.CircuitInfo getCircuitInfo() {
        return new Formats.CircuitInfo(inputsN, rows, columns, l, maxArity);
    }
}
