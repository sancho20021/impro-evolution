package algorithm;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static algorithm.Pair.IntPair;


public class GenomeOperations {
    public final CircuitInfo circuitInfo;
    public final Random random;
    public final int genesNumber;
    private double c = 1; // Controls mutation rate. Mu = c / genesNumber
    private double f = 0;  // Probability of patch cord going forward (for feedback loops)
    private double d = 0.5; // Controls the ratio between mutation of double parameters and integer p.
    // Pr(real parameter mutation) / Pr(int parameter mutation) = d / (1 - d)
    // d == 0.5 -> real parameter and integer parameter have equal chance of mutation
    // d == 1 -> integer parameters don't mutate

    private final static DoubleRange F_RANGE = new DoubleRange(0, 1, true, false);


    public GenomeOperations(final CircuitInfo info, final Random random) {
        this.circuitInfo = info;
        this.random = random;
        genesNumber = circuitInfo.inputsN + (circuitInfo.maxArity + 1) * circuitInfo.getModulesN() + 1;
    }

    public double getC() {
        return c;
    }

    public boolean setC(final double c) {
        if (getCRange().isInSet(c)) {
            this.c = c;
            return true;
        }
        return false;
    }

    public DoubleRange getCRange() {
        final double n = genesNumber, m = circuitInfo.inputsN;
        return new DoubleRange(0, Math.min(n, (d * m + (1 - d) * (n - m)) / Math.max(d, 1 - d)), false, true);
    }

    public double getD() {
        return d;
    }

    public boolean setD(final double d) {
        if (getDRange().isInSet(d)) {
            this.d = d;
            return true;
        }
        return false;
    }

    public DoubleRange getDRange() {
        final double n = genesNumber, m = circuitInfo.inputsN;
        return new DoubleRange(
                c <= n - m ? 0 : (c - n + m) / (2 * m + c - n),
                c <= m ? 1 : (n - m) / (c - 2 * m + n),
                true,
                true
        );
    }

    public boolean setF(final double f) {
        if (getFRange().isInSet(f)) {
            this.f = f;
            return true;
        }
        return false;
    }

    public double getF() {
        return f;
    }

    public DoubleRange getFRange() {
        return F_RANGE;
    }

    public Genome generateGenome() {
        final double[] inputs = new double[circuitInfo.inputsN];
        final int[][] modules = new int[circuitInfo.getModulesN()][circuitInfo.maxArity + 1];
        int output;
        final int fN = MusicCircuit.MODULES.size();
        for (int i = 0; i < modules.length; i++) {
            modules[i][0] = random.nextInt(fN);
            final var argBounds = getModuleArgBounds(i);
            for (int j = 1; j < modules[i].length; j++) {
                modules[i][j] = argBounds.getA() + random.nextInt(argBounds.getB() - argBounds.getA());
            }
        }
        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = random.nextDouble() * 1000;  // Consider other methods of generating input constants
        }
//        output = random.nextInt(inputs.length + modules.length);
        output = inputs.length + modules.length - 1;

        return new Genome(inputs, modules, output);
    }

    private void mutate(final Genome genome, final int gene, final IntPair inputGenes, final IntPair moduleGenes) {
        if (inputGenes.getA() <= gene && gene < inputGenes.getB()) {
            genome.inputs[gene] = random.nextDouble() * 1000;
        } else if (moduleGenes.getA() <= gene && gene < moduleGenes.getB()) {
            final int moduleN = (gene - moduleGenes.getA()) / (circuitInfo.maxArity + 1);
            final int moduleParameter = (gene - moduleGenes.getA()) % (circuitInfo.maxArity + 1);
            if (moduleParameter == 0) {
                genome.modules[moduleN][0] = random.nextInt(MusicCircuit.MODULES.size());
            } else {
                final var argBounds = getModuleArgBounds(moduleN);
                genome.modules[moduleN][moduleParameter] = argBounds.getA() + random.nextInt(argBounds.getB() - argBounds.getA());
            }
        } else {
//            genome.output = random.nextInt(circuitInfo.getInputsN() + circuitInfo.getModulesN());
            genome.output = genome.inputs.length + genome.modules.length - 1;
        }
    }

    private Genome mutate(final Genome genome, final List<Integer> genes) {
        final Genome newGenome = genome.getCopy();
        final var inputGenes = new IntPair(0, circuitInfo.inputsN);
        final var moduleGenes = new IntPair(inputGenes.getB(), (circuitInfo.getModulesN()) * (circuitInfo.maxArity + 1));
        genes.forEach(gene -> mutate(newGenome, gene, inputGenes, moduleGenes));
        return newGenome;
    }

    private double getMutationPr(final int gene) {
        final int n = genesNumber, m = circuitInfo.inputsN;
        if (gene < m) {
            return (d * c) / (d * m + (1 - d) * (n - m));
        } else {
            return ((1 - d) * c) / (d * m + (1 - d) * (n - m));
        }
    }

    public Genome mutate(final Genome genome) {
        return mutate(genome, IntStream.range(0, genesNumber).filter(i -> random.nextDouble() <= getMutationPr(i)).boxed().collect(Collectors.toList()));
    }

    private IntPair getModuleArgBounds(final int module) {
        if (!circuitInfo.isLineOut(module) && random.nextDouble() >= f) {
            final int column = module / circuitInfo.rows;
            final int fromColumnN = (column >= circuitInfo.l) ? (column - circuitInfo.l) * circuitInfo.rows + circuitInfo.inputsN : 0;
            final int toColumnN = circuitInfo.inputsN + column * circuitInfo.rows;
            return new IntPair(fromColumnN, toColumnN);
        } else {
            return new IntPair(module + 1, circuitInfo.inputsN + circuitInfo.getModulesN());
        }
    }
}
