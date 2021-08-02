package algorithm;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class GenomeOperations {
    public final CircuitInfo circuitInfo;
    public final Random random;
    public final int genesNumber;
    private double forwardCordsPr = 0;  // Probability of cord going forward (for feedback loops)


    public GenomeOperations(final CircuitInfo info, final Random random) {
        this.circuitInfo = info;
        this.random = random;
        genesNumber = circuitInfo.inputsN + (circuitInfo.maxArity + 1) * circuitInfo.getModulesN() + 1;
    }

    public void setForwardCordsPr(final double forwardCordsPr) {
        if (!(0 <= forwardCordsPr && forwardCordsPr < 1)) {
            throw new IllegalArgumentException("forwardCordsPr must be in [0, 1)");
        }
        this.forwardCordsPr = forwardCordsPr;
    }

    public double getForwardCordsPr() {
        return forwardCordsPr;
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
                modules[i][j] = argBounds.a + random.nextInt(argBounds.b - argBounds.a);
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
        if (inputGenes.a <= gene && gene < inputGenes.b) {
            genome.inputs[gene] = random.nextDouble();
        } else if (moduleGenes.a <= gene && gene < moduleGenes.b) {
            final int moduleN = (gene - moduleGenes.a) / (circuitInfo.maxArity + 1);
            final int moduleParameter = (gene - moduleGenes.a) % (circuitInfo.maxArity + 1);
            if (moduleParameter == 0) {
                genome.modules[moduleN][0] = random.nextInt(MusicCircuit.MODULES.size());
            } else {
                final var argBounds = getModuleArgBounds(moduleN);
                genome.modules[moduleN][moduleParameter] = argBounds.a + random.nextInt(argBounds.b - argBounds.a);
            }
        } else {
//            genome.output = random.nextInt(circuitInfo.getInputsN() + circuitInfo.getModulesN());
            genome.output = genome.inputs.length + genome.modules.length - 1;
        }
    }

    private Genome mutate(final Genome genome, final List<Integer> genes) {
        final Genome newGenome = genome.getCopy();
        final var inputGenes = new IntPair(0, circuitInfo.inputsN);
        final var moduleGenes = new IntPair(inputGenes.b, (circuitInfo.getModulesN()) * (circuitInfo.maxArity + 1));
        genes.forEach(gene -> mutate(newGenome, gene, inputGenes, moduleGenes));
        return newGenome;
    }

    public Genome mutate(final Genome genome, final double mutationRate) {
        return mutate(genome, IntStream.range(0, genesNumber).filter(i -> random.nextDouble() <= mutationRate).boxed().collect(Collectors.toList()));
    }

    private IntPair getModuleArgBounds(final int module) {
        if (!circuitInfo.isLineOut(module) && random.nextDouble() >= forwardCordsPr) {
            final int column = module / circuitInfo.rows;
            final int fromColumnN = (column >= circuitInfo.l) ? (column - circuitInfo.l) * circuitInfo.rows + circuitInfo.inputsN : 0;
            final int toColumnN = circuitInfo.inputsN + column * circuitInfo.rows;
            return new IntPair(fromColumnN, toColumnN);
        } else {
            return new IntPair(module + 1, circuitInfo.inputsN + circuitInfo.getModulesN());
        }
    }

    static class IntPair {
        final public int a, b;

        public IntPair(final int a, final int b) {
            this.a = a;
            this.b = b;
        }
    }
}
