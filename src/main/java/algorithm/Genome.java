package algorithm;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Genome {
    final public double[] inputs;
    final public int[][] modules;
    public int output;

    public Genome(final double[] inputs, final int[][] modules, final int output) {
        this.inputs = inputs;
        this.modules = modules;
        this.output = output;
    }

    public Genome getCopy() {
        final double[] newInputs = Arrays.copyOf(inputs, inputs.length);
        final int[][] newModules = new int[modules.length][];
        for (int i = 0; i < modules.length; i++) {
            newModules[i] = Arrays.copyOf(modules[i], modules[i].length);
        }
        return new Genome(newInputs, newModules, output);
    }

    @Override
    public String toString() {
        return Arrays.stream(inputs)
                .mapToObj(Double::toString)
                .collect(Collectors.joining(" "))
                + " "
                + Arrays.stream(modules)
                .map(f -> Arrays.stream(f)
                        .mapToObj(Integer::toString)
                        .collect(Collectors.joining("|", "<", ">"))
                ).collect(Collectors.joining(""))
                + output;

    }
}
