package algorithm;

import algorithm.formats.CircuitInfo;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.*;
import modules.Module;
import modules.Modules;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MusicCircuit {
    private Synthesizer synthesizer;
    private final Modules.Constant[] inputs;
    private final Module[] modules;
    private Modules.LineOutModule output;
    public final int rows, columns, l, maxArity;
    private final List<Supplier<Module>> availableModules;
    public Genome genome;
    public CompactCircuit compactCircuit;
    public final static List<Supplier<Module>> STANDARD_MODULES = List.of(
            () -> new Modules.Oscillator(new SineOscillator()),  //     0
            () -> new Modules.Oscillator(new TriangleOscillator()),  // 1
            () -> new Modules.Oscillator(new SawtoothOscillator()),  // 2
            () -> new Modules.Oscillator(new SquareOscillator()),  //   3
            () -> new Modules.BinaryOperator(new Add()),  //            4
            () -> new Modules.BinaryOperator(new Subtract()),  //       5
            () -> new Modules.BinaryOperator(new Multiply()),  //       6
            () -> new Modules.BinaryOperator(new Divide()),  //         7
//            modules.Modules.PowerOfTwoModule::new,
            Modules.SelectModule::new,  //                              8
            Modules.Two::new,  //                                       9
            () -> new Modules.TunableFilterModule(new FilterLowPass()),  // 10
            () -> new Modules.TunableFilterModule(new FilterHighPass()),  // 11
            () -> new Modules.TunableFilterModule(new FilterBandPass()),  // 12
            Modules.LatchModule::new,  //                                    13
            () -> new Modules.BinaryOperator(new Minimum()),  //             14
            () -> new Modules.BinaryOperator(new Maximum())  //              15
    );

    public MusicCircuit(final int inputsN, final int rows, final int columns, final int l, List<Supplier<Module>> availableModules) {
        inputs = new Modules.Constant[inputsN];
        this.rows = rows;
        this.columns = columns;
        this.modules = new Module[rows * columns];
        this.l = l;
        this.availableModules = availableModules;
        int m = 0;
        for (final var moduleC : availableModules) {
            m = Math.max(m, moduleC.get().getInputsNumber());
        }
        maxArity = m;
    }

    public MusicCircuit(final int inputsN, final int rows, final int columns, final int l) {
        this(inputsN, rows, columns, l, STANDARD_MODULES);
    }

    public MusicCircuit(final CircuitInfo circuitInfo) {
        this(circuitInfo.inputsN, circuitInfo.rows, circuitInfo.columns, circuitInfo.l);
    }


    private void addModule(final Module module) {
        synthesizer.add(module.getUnitGenerator());
    }

    public boolean isInput(final int n) {
        return 0 <= n && n < inputs.length;
    }

    public boolean isModule(final int n) {
        return inputs.length <= n && n - inputs.length < rows * columns;
    }

    public boolean isLineOut(final int n) {
        return n == inputs.length + rows * columns;
    }

    public void applyGenome(final Genome genome) {
        synthesizer = JSyn.createSynthesizer();  // Create synthesizer
        output = new Modules.LineOutModule();  // Create output
        addModule(output);

        this.genome = genome;  // Save genome

        // initialize inputs
        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = new Modules.Constant(genome.inputs[i]);
            addModule(inputs[i]);
        }
        // initialize module types
        for (int i = 0; i < modules.length; i++) {
            modules[i] = availableModules.get(genome.modules[i][0]).get();
            addModule(modules[i]);
        }
        compactCircuit = CompactCircuit.fromMusicCircuit(this);
        // Bind modules together
        bindModules();
    }

    private void bindModules() {
        for (final var node : compactCircuit.modules.entrySet()) {
            for (int i = 0; i < node.getValue().arguments.size(); i++) {
                connect(node.getValue().arguments.get(i), node.getKey(), i);
            }
        }
        // TODO stereo instead of mono (only left channel sounds)
        connect(compactCircuit.outputSource, compactCircuit.outputN, 0);
    }

    public boolean[] getActiveModules() {
        final boolean[] active = new boolean[inputs.length + modules.length + 1];
        for (final int i : compactCircuit.inputs.keySet()) {
            active[i] = true;
        }
        for (final int i : compactCircuit.modules.keySet()) {
            active[i] = true;
        }
        active[compactCircuit.outputN] = true;
        return active;
    }

    private Module getModule(final int n) {
        if (isInput(n)) {
            return inputs[n];
        } else if (isModule(n)) {
            return modules[n - inputs.length];
        } else if (isLineOut(n)) {
            return output;
        } else {
            throw new IllegalArgumentException("No such module: " + n);
        }
    }

    private void connect(final int module1, final int module2, final int module2Port) {
        if (isInput(module2) || isLineOut(module1)) {
            throw new IllegalArgumentException("Can't connect modules " + module1 + ", " + module2);
        }
        getModule(module1).getOutput().connect(getModule(module2).getInput(module2Port));
    }

    private List<Integer> getArgumentNodes(final int n) {
        if (isLineOut(n)) {
            return List.of(genome.output);
        } else if (isModule(n)) {
            final List<Integer> args = new ArrayList<>();
            for (int i = 1; i <= modules[n - inputs.length].getInputsNumber(); i++) {
                args.add(genome.modules[n - inputs.length][i]);
            }
            return args;

        } else {
            return Collections.emptyList();
        }
    }

    public Synthesizer getSynthesizer() {
        return synthesizer;
    }

    public int getModulesN() {
        return modules.length;
    }

    public int getInputsN() {
        return inputs.length;
    }

    public int getAvailableModulesN() {
        return availableModules.size();
    }

    public void playFor(final double time) throws InterruptedException {
        if (genome == null) {
            throw new IllegalStateException("No genome. Can't play music");
        }
        synthesizer.start();
        output.getUnitGenerator().start();
        try {
            synthesizer.sleepUntil(synthesizer.getCurrentTime() + time);
        } finally {
            synthesizer.stop();
        }
    }

    public static class Node {
        private int type;
        private List<Integer> arguments;

        public Node(final int type, final List<Integer> arguments) {
            this.type = type;
            this.arguments = arguments;
        }

        public Node(final int type) {
            this(type, new ArrayList<>());
        }

        public Node(final int[] typeAndArguments) {
            this(
                    typeAndArguments[0],
                    Arrays.stream(Arrays.copyOfRange(typeAndArguments, 1, typeAndArguments.length))
                            .boxed()
                            .collect(Collectors.toList())
            );
        }

        public int getType() {
            return type;
        }

        public List<Integer> getArguments() {
            return arguments;
        }
    }

    public static class CompactCircuit {
        private final Map<Integer, Double> inputs;
        private final Map<Integer, Node> modules;
        private final int outputN;
        private int outputSource;

        public CompactCircuit(final int size) {
            inputs = new HashMap<>();
            modules = new TreeMap<>();
            outputN = size - 1;
        }

        public static CompactCircuit fromMusicCircuit(final MusicCircuit circuit) {
            final var compactCircuit = new CompactCircuit(circuit.getInputsN() + circuit.getModulesN() + 1);
            final boolean[] active = new boolean[circuit.inputs.length + circuit.modules.length + 1];
            final Queue<Integer> bfsQueue = new LinkedList<>(List.of(circuit.inputs.length + circuit.modules.length));
            while (!bfsQueue.isEmpty()) {
                final int node = bfsQueue.remove();
                if (active[node]) {
                    continue;
                }
                active[node] = true;
                if (circuit.isInput(node)) {
                    compactCircuit.inputs.put(node, circuit.genome.inputs[node]);
                } else if (circuit.isModule(node)) {
                    compactCircuit.modules.put(node, new Node(circuit.genome.modules[node - circuit.inputs.length][0], circuit.getArgumentNodes(node)));
                } else {
                    compactCircuit.outputSource = circuit.genome.output;
                }
                circuit.getArgumentNodes(node).stream().filter(arg -> !active[arg]).forEach(bfsQueue::add);
            }
            return compactCircuit;
        }

        public Map<Integer, Double> getInputs() {
            return inputs;
        }

        public Map<Integer, Node> getModules() {
            return modules;
        }

        public int getOutputSource() {
            return outputSource;
        }

        public int getOutputN() {
            return outputN;
        }
    }
}
