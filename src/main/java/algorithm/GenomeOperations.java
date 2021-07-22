package algorithm;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class GenomeOperations {
    public final MusicCircuit circuit;
    public final Random random;
    public final int genesNumber;
    private double forwardCordsPr = 0;  // Probability of cord going forward (for feedback loops)


    public GenomeOperations(final MusicCircuit circuit, final Random random) {
        this.circuit = circuit;
        this.random = random;
        genesNumber = circuit.getInputsN() + (circuit.maxArity + 1) * circuit.getModulesN() + 1;
    }

    public void setForwardCordsPr(final double forwardCordsPr) {
        if (!(0 <= forwardCordsPr && forwardCordsPr < 1)) {
            throw new IllegalArgumentException("forwardCordsPr must be in [0, 1)");
        }
        this.forwardCordsPr = forwardCordsPr;
    }

    public Genome generateGenome() {
        final double[] inputs = new double[circuit.getInputsN()];
        final int[][] modules = new int[circuit.getModulesN()][circuit.maxArity + 1];
        int output;
        final int fN = circuit.getAvailableModulesN();
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
            final int moduleN = (gene - moduleGenes.a) / (circuit.maxArity + 1);
            final int moduleParameter = (gene - moduleGenes.a) % (circuit.maxArity + 1);
            if (moduleParameter == 0) {
                genome.modules[moduleN][0] = random.nextInt(circuit.getAvailableModulesN());
            } else {
                final var argBounds = getModuleArgBounds(moduleN);
                genome.modules[moduleN][moduleParameter] = argBounds.a + random.nextInt(argBounds.b - argBounds.a);
            }
        } else {
//            genome.output = random.nextInt(circuit.getInputsN() + circuit.getModulesN());
            genome.output = genome.inputs.length + genome.modules.length - 1;
        }
    }

    private Genome mutate(final Genome genome, final List<Integer> genes) {
        final Genome newGenome = genome.getCopy();
        final var inputGenes = new IntPair(0, circuit.getInputsN());
        final var moduleGenes = new IntPair(inputGenes.b, (circuit.getModulesN()) * (circuit.maxArity + 1));
        genes.forEach(gene -> mutate(newGenome, gene, inputGenes, moduleGenes));
        return newGenome;
    }

    public Genome mutate(final Genome genome, final double mutationRate) {
        return mutate(genome, IntStream.range(0, genesNumber).filter(i -> random.nextDouble() <= mutationRate).boxed().collect(Collectors.toList()));
    }

    private IntPair getModuleArgBounds(final int module) {
        if (!circuit.isLineOut(module) && random.nextDouble() >= forwardCordsPr) {
            final int column = module / circuit.rows;
            final int fromColumnN = (column >= circuit.l) ? (column - circuit.l) * circuit.rows + circuit.getInputsN() : 0;
            final int toColumnN = circuit.getInputsN() + column * circuit.rows;
            return new IntPair(fromColumnN, toColumnN);
        } else {
            return new IntPair(module + 1, circuit.getInputsN() + circuit.getModulesN());
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
