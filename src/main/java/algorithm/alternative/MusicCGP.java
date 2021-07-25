package algorithm.alternative;

import algorithm.Genome;
import algorithm.formats.CircuitInfo;
import com.jsyn.JSyn;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.Pan;
import com.jsyn.unitgen.UnitGenerator;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MusicCGP {
    private final CircuitInfo circuitInfo;
    private final GenomeOperations genomeOperations;
    public final int lambda;
    public double c; // c. Pr of mutation for each gene is c / number of genes
    private int maxPopulations = Integer.MAX_VALUE;
    private final Scanner scn = new Scanner(System.in);
    private double seconds = 2;
    //    private final Logger logger;
    private MusicCircuit circuit;
    private int pannedTracksN;

    public MusicCGP(final CircuitInfo info, final int lambda, final double c) {
        this(info, lambda, c, 0);
    }

    public MusicCGP(final CircuitInfo info, final int lambda, final double c, final double forwardCordsPr) {
        this.circuitInfo = info;
        genomeOperations = new GenomeOperations(circuitInfo, new Random(2));
        genomeOperations.setForwardCordsPr(forwardCordsPr);
        this.lambda = lambda;
        this.c = c;
        this.pannedTracksN = 1;

//        logger = new Logger(circuitInfo);
    }

    public MusicCGP(final CircuitInfo info) {
        this(info, 4, 2);
    }

    public void setPannedTracksN(final int n) {
        if (n < 1) {
            throw new IllegalArgumentException("Expected positive number");
        }
        pannedTracksN = n;
    }

    public void setForwardCordsPr(final double forwardCordsPr) {
        genomeOperations.setForwardCordsPr(forwardCordsPr);
    }

    public void setMaxPopulations(final int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("maxPopulations must be positive");
        }
        maxPopulations = n;
    }

    public void setListeningTime(final double seconds) {
        if (seconds <= 0) {
            throw new IllegalArgumentException("Seconds must be positive");
        }
        this.seconds = seconds;
    }

//    public static Genome pickUpMelody(final OneMelody oneMelody) {
//        final var cgp = new MusicCGP(new algorithm.MusicCircuit(oneMelody.circuitInfo));
//        return cgp.evolve(oneMelody.genome);
//    }

//    public static Genome pickUpMelody(final Composition composition) {
//        final var cgp = new MusicCGP(new algorithm.MusicCircuit(composition.inputsN, composition.rows, composition.columns, composition.l));
//        return cgp.evolve(composition.getGenomes().get(composition.getGenomes().size() - 1));
//    }

//    public static void playComposition(final Composition composition, final double secondsPerMelody) {
//        final var circuit = new algorithm.MusicCircuit(composition.getCircuitInfo());
//        circuit.applyGenome(composition.getGenomes().get(0));
//        final var cgp = new MusicCGP(circuit);
//        cgp.setListeningTime(secondsPerMelody);
//        for (final var genome : composition.getGenomes()) {
//            cgp.play(genome);
//        }
//    }
//    public static void playMelody(final OneMelody melody, final double secondsToPlay) {
//        final var circuit = new MusicCircuit(melody.circuitInfo);
//        circuit.applyGenome(melody.genome);
//        final var cgp = new MusicCGP(circuit);
//        cgp.setListeningTime(secondsToPlay);
//        cgp.play(circuit.genome);
//    }

    public Genome evolve() {
        return evolve(genomeOperations.generateGenome());
    }

    private void listCommands() {
        System.out.println(
                "'help' to see commands.\n" +
                        "'stop' if you are finished with composing.\n" +
                        "'set m <number>' to change mutation rate (from 1 to +inf).\n" +
                        "'set time <number>' to change listening time.\n" +
                        "'set f <number>' to change probability of forward cords (from 0 to 1)'.\n" +
                        "'current' to listen to current melody.\n" +
                        "'repeat' to listen to suggested melodies.\n" +
                        "'save' to save current melody.\n" +
                        "number from 1 to " + lambda + " to choose favourite melody or '0' if you don't like them"
        );
    }

    public Genome evolve(Genome genome) {
//        try {
//            logger.ensureDirectoryExists();
//        } catch (final IOException e) {
//            System.err.println("Error, music won't be saved: " + e.getMessage());
//        }
        listCommands();
        circuit = new MusicCircuit(circuitInfo, genome);
//        logger.addGenome(genome);
        outer:
        for (int i = 0; i < maxPopulations; i++) {
            final var offspring = new Genome[lambda];
            for (int j = 0; j < offspring.length; j++) {
                offspring[j] = getNextMutant(genome);
            }
            announceMelodies(offspring);

            AtomicInteger n = new AtomicInteger(-1);
            while (n.get() == -1) {
                final String input = scn.nextLine();
                if (input.equals("help")) {
                    listCommands();
                } else if (input.equals("stop")) {
                    break outer;
                } else if (input.equals("current")) {
                    System.out.println("Playing current melody");
                    playParallel(List.of(circuit));
                    System.out.println("Playing stopped. Type command");
                } else if (input.equals("repeat")) {
                    System.out.println("Playing melodies one more time");
                    announceMelodies(offspring);
                } /*else if (input.equals("save")) {
                    System.out.println("Saving current melody to file " + circuit.genome.hashCode());
                    try {
                        logger.putObject(
                                Integer.toString(circuit.genome.hashCode()),
                                new OneMelody(new CircuitInfo(circuit), circuit.genome)
                        );
                    } catch (final IOException e) {
                        System.err.println("Couldn't save melody: " + e.getMessage());
                        e.printStackTrace();
                    }
                }*/ else if (input.matches("set \\S+ \\S+")) {
                    final var words = input.split("\\s");
                    switch (words[1]) {
                        case "m":
                            computeData(
                                    words[2],
                                    Double::parseDouble,
                                    x -> x > 0,
                                    newC -> {
                                        c = newC;
                                        System.out.println("Mutation set to " + c);
                                    },
                                    "Only doubles > 0 allowed for parameter 'm'"
                            );
                            break;
                        case "time":
                            computeData(
                                    words[2],
                                    Double::parseDouble,
                                    x -> x > 0,
                                    newTime -> {
                                        seconds = newTime;
                                        System.out.println("Time set to " + newTime);
                                    },
                                    "Only doubles > 0 allowed for parameter 'time'"
                            );
                            break;
                        case "f":
                            computeData(
                                    words[2],
                                    Double::parseDouble,
                                    x -> 0 <= x && x < 1,
                                    newF -> {
                                        setForwardCordsPr(newF);
                                        System.out.println("f set to " + newF);
                                    },
                                    "f must be in [0, 1)"
                            );
                            break;
                        default:
                            System.out.println("No such parameter '" + words[1] + "'");
                    }
                } else {
                    computeData(
                            input,
                            Integer::parseInt,
                            x -> 0 <= x && x <= offspring.length,
                            n::set,
                            "Unrecognized command"
                    );
                }
            }
            if (n.get() > 0) {
                genome = offspring[n.get() - 1];
                circuit = new MusicCircuit(circuitInfo, genome);
//                logger.addGenome(genome);
            }
        }
//        try {
//            logger.saveToFile();
//        } catch (final IOException e) {
//            System.err.println("Couldn't save composition: " + e.getMessage());
//            e.printStackTrace();
//        }
        return genome;
    }

    private static <T> void computeData(
            final String string,
            final Function<String, T> f,
            final Predicate<T> predicate,
            final Consumer<T> computeFun,
            final String errorMessage
    ) {
        try {
            final T data = f.apply(string);
            if (predicate.test(data)) {
                computeFun.accept(data);
            } else {
                System.out.println(errorMessage);
            }
        } catch (final RuntimeException e) {
            System.out.println(errorMessage);
        }
    }

    private Genome getNextMutant(final Genome genome) {
        while (true) {
            final var nextGenome = genomeOperations.mutate(genome, c / genomeOperations.genesNumber);
            final var diff = getDifferentActiveNodes(nextGenome);
            if (!diff.isEmpty() && isValidGenome(nextGenome)) {
                return nextGenome;
            }
        }
    }

    private boolean isValidGenome(final Genome genome) {
        return true;
    }

    private List<Integer> getDifferentActiveNodes(final Genome other) {
        final List<Integer> diff = new ArrayList<>();
        for (final var module : circuit.getModules().entrySet()) {
            final int index = module.getKey();
            if (circuitInfo.isInput(index) && circuit.genome.inputs[index] != other.inputs[index]) {
                diff.add(index);
            } else if (circuitInfo.isModule(index)) {
                final int moduleIndex = index - circuitInfo.inputsN;
                final int argsNumber = module.getValue().getArguments().size();
                for (int i = 0; i <= argsNumber; i++) {
                    if (circuit.genome.modules[moduleIndex][i] != other.modules[moduleIndex][i]) {
                        diff.add(index);
                    }
                }
            }
        }
        if (circuit.genome.output != other.output) {
            diff.add(circuit.genome.inputs.length + circuit.genome.modules.length);
        }
        return diff;
    }

    private void playParallel(List<MusicCircuit> circuits) {
        final var out = new LineOut();
        final List<Pan> pans = new ArrayList<>(circuits.size());
        for (int i = 0; i < circuits.size(); i++) {
            final var pan = new Pan();
            circuits.get(i).getOutput().connect(pan.input);
            pan.pan.set(-1 + 2.0 / (circuits.size() - 1) * i);
            pan.output.connect(0, out.input, 0);
            pan.output.connect(1, out.input, 1);
            pans.add(pan);
        }
        final List<UnitGenerator> units = new ArrayList<>(circuits);
        units.addAll(pans);
        play(units, out, seconds);
    }

    public static void play(final List<UnitGenerator> units, final LineOut out, final double secondsToPlay) {
        final var synth = JSyn.createSynthesizer();
        units.forEach(synth::add);
        synth.add(out);
        synth.start();
        out.start();
        try {
            synth.sleepFor(secondsToPlay);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } finally {
            synth.stop();
        }
    }

    private void announceMelodies(final Genome[] genomes) {
        System.out.println("You will hear " + lambda + " different melodies. Choose the most favourite");
        final var instruments = Arrays.stream(genomes).map(gen -> new MusicCircuit(circuitInfo, gen)).collect(Collectors.toList());
        for (int groupI = 0; groupI < (genomes.length + pannedTracksN - 1) / pannedTracksN; groupI++) {
            final int from = groupI * pannedTracksN, to = Math.min(instruments.size(), (groupI + 1) * pannedTracksN);
            System.out.println("Melodies " + (from + 1) + "..." + to + " playing...");
            playParallel(instruments.subList(from, to));
            System.out.println("Melodies playing finished");
        }
    }

}
