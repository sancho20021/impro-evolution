package algorithm;

import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.*;
import modules.Module;
import modules.Modules;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MusicCircuit extends Circuit implements UnitSource {
    public final static List<Supplier<Module>> MODULES = List.of(
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
    protected final UnitOutputPort output;
    private final CircuitInfo info;
    private final Graph graph = new Graph();
    public final Genome genome;

    public MusicCircuit(final CircuitInfo info, final Genome genome) {
        this.info = info;
        this.genome = genome;

        final boolean[] active = new boolean[info.inputsN + genome.modules.length + 1];
        final Queue<Integer> bfsQueue = new LinkedList<>(List.of(genome.output));
        final Map<Integer, Module> modules = new TreeMap<>();
        while (!bfsQueue.isEmpty()) {
            final int node = bfsQueue.remove();
            if (active[node]) {
                continue;
            }
            active[node] = true;
            final var module = info.isInput(node)
                    ? new Modules.Constant(genome.inputs[node])
                    : MODULES.get(genome.modules[node - info.inputsN][0]).get();
            add(module.getUnitGenerator());
            modules.put(node, module);
            final List<Integer> args = getArguments(node, module);
            graph.modules.put(node, new ModuleNode(node, args));
            if (info.isInput(node)) {
                graph.inputs.put(node, genome.inputs[node]);
            }
            args.stream().filter(arg -> !active[arg]).forEach(bfsQueue::add);
        }

        // bind modules
        for (final var entry : graph.modules.entrySet()) {
            final var module = modules.get(entry.getKey());
            final var args = entry.getValue().getArguments().stream().map(modules::get).collect(Collectors.toList());
            for (int i = 0; i < args.size(); i++) {
                args.get(i).getOutput().connect(module.getInput(i));
            }
        }
        addPort(output = modules.get(genome.output).getOutput());
    }

    @Override
    public UnitOutputPort getOutput() {
        return output;
    }

    @Override
    public UnitGenerator getUnitGenerator() {
        return this;
    }

    private List<Integer> getArguments(final int n, final Module module) {
        if (info.isInput(n)) {
            return List.of();
        } else if (info.isLineOut(n)) {
            return List.of(genome.output);
        }
        final List<Integer> args = new ArrayList<>();
        for (int i = 0; i < module.getInputsNumber(); i++) {
            args.add(genome.modules[n - info.inputsN][i + 1]);
        }
        return args;
    }

    public Graph getGraph() {
        return graph;
    }

    public Map<Integer, ModuleNode> getModules() {
        return graph.modules;
    }


    public static class ModuleNode {
        private final int module;
        private final List<Integer> arguments;

        public ModuleNode(final int module, final List<Integer> arguments) {
            this.module = module;
            this.arguments = arguments;
        }

        public int getModule() {
            return module;
        }

        public List<Integer> getArguments() {
            return arguments;
        }
    }

    public static class Graph {
        final Map<Integer, Double> inputs = new TreeMap<>();
        final Map<Integer, ModuleNode> modules = new TreeMap<>();
    }

    public static int getMaxArity() {
        return MODULES.stream()
                .map(Supplier::get)
                .mapToInt(Module::getInputsNumber)
                .max()
                .orElseThrow(() -> new IllegalStateException("Empty MODULES list"));
    }
}
