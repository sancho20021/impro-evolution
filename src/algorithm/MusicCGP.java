package algorithm;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class MusicCGP {
    private final MusicCircuit circuit;
    private final GenomeOperations genomeOperations;
    public final int lambda;
    public double c; // c. Pr of mutation for each gene is c / number of genes
    private int maxPopulations = Integer.MAX_VALUE;
    private final Scanner scn = new Scanner(System.in);
    private double seconds = 2;
    private final Logger logger;

    public MusicCGP(
            final MusicCircuit circuit,
            final int lambda,
            final double c
    ) {
        this.circuit = circuit;
        genomeOperations = new GenomeOperations(circuit, new Random(2));
        this.lambda = lambda;
        this.c = c;
        logger = new Logger(circuit);
    }

    public MusicCGP(final MusicCircuit circuit) {
        this(circuit, 4, 2);
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

    public static Genome pickUpMelody(final Formats.OneMelody oneMelody) {
        final var cgp = new MusicCGP(new MusicCircuit(oneMelody.circuitInfo));
        return cgp.evolve(oneMelody.genome);
    }

    public static Genome pickUpMelody(final Composition composition) {
        final var cgp = new MusicCGP(new MusicCircuit(composition.inputsN, composition.rows, composition.columns, composition.l));
        return cgp.evolve(composition.getGenomes().get(composition.getGenomes().size() - 1));
    }

    // TODO убрать костыль с circuit.applyGenome() в начале
    public static void playComposition(final Composition composition, final double secondsPerMelody) {
        final var circuit = new MusicCircuit(composition.getCircuitInfo());
        circuit.applyGenome(composition.getGenomes().get(0));
        final var cgp = new MusicCGP(circuit);
        cgp.setListeningTime(secondsPerMelody);
        for (final var genome : composition.getGenomes()) {
            cgp.play(genome);
        }
    }

    public Genome evolve() {
        return evolve(genomeOperations.generateGenome());
    }

    private void listCommands() {
        System.out.println(
                "'help' to see commands.\n" +
                        "'stop' if you are finished with composing.\n" +
                        "'set m <number>' to change mutation rate (from 1 to +inf).\n" +
                        "'set time <number>' to change listening time.\n" +
                        "'current' to listen to current melody.\n" +
                        "'repeat' to listen to suggested melodies.\n" +
                        "'save' to save current melody.\n" +
                        "number from 1 to " + lambda + " to choose favourite melody or '0' if you don't like them"
        );
    }

    public Genome evolve(Genome genome) {
        try {
            logger.ensureDirectoryExists();
        } catch (final IOException e) {
            System.err.println("Error, music won't be saved: " + e.getMessage());
        }
        listCommands();
        circuit.applyGenome(genome);
        logger.addGenome(genome);
        outer:
        for (int i = 0; i < maxPopulations; i++) {
            final var offspring = new Genome[lambda];
            for (int j = 0; j < offspring.length; j++) {
                offspring[j] = getNextMutant();
            }
            announceMelodies(offspring);

            int n = -1;
            while (n == -1) {
                final String input = scn.nextLine();
                if (input.equals("help")) {
                    listCommands();
                } else if (input.equals("stop")) {
                    break outer;
                } else if (input.equals("current")) {
                    System.out.println("Playing current melody");
                    play(circuit.genome);
                    System.out.println("Playing stopped. Type command");
                } else if (input.equals("repeat")) {
                    System.out.println("Playing melodies one more time");
                    announceMelodies(offspring);
                } else if (input.equals("save")) {
                    System.out.println("Saving current melody to file " + circuit.genome.hashCode());
                    try {
                        logger.putObject(
                                Integer.toString(circuit.genome.hashCode()),
                                new Formats.OneMelody(new Formats.CircuitInfo(circuit), circuit.genome)
                        );
                    } catch (final IOException e) {
                        System.err.println("Couldn't save melody: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else if (input.matches("set \\S+ \\S+")) {
                    final var words = input.split("\\s");
                    if (words[1].equals("m")) {
                        final var newC = computeData(words[2], Double::parseDouble, x -> x > 0, "Only doubles > 0 allowed for parameter 'm'");
                        newC.ifPresent(m -> c = m);
                        System.out.println("Mutation set to " + c);
                    } else if (words[1].equals("time")) {
                        final var newTime = computeData(words[2], Double::parseDouble, x -> x > 0, "Only doubles > 0 allowed for parameter 'time'");
                        newTime.ifPresent(t -> seconds = t);
                        System.out.println("Time set to " + seconds);
                    } else {
                        System.out.println("No such parameter '" + words[1] + "'");
                    }
                } else {
                    final var melodyN = computeData(input, Integer::parseInt, x -> 0 <= x && x <= offspring.length, "Unrecognized command");
                    if (melodyN.isPresent()) {
                        n = melodyN.get();
                    }
                }
            }
            if (n > 0) {
                genome = offspring[n - 1];
                circuit.applyGenome(genome);
                logger.addGenome(genome);
            }
        }
        try {
            logger.saveToFile();
        } catch (final IOException e) {
            System.err.println("Couldn't save composition: " + e.getMessage());
            e.printStackTrace();
        }
        return genome;
    }

    private static <T> Optional<T> computeData(final String string, final Function<String, T> f, final Predicate<T> predicate, final String errorMessage) {
        try {
            final T data = f.apply(string);
            if (predicate.test(data)) {
                return Optional.of(data);
            } else {
                System.out.println(errorMessage);
            }
        } catch (final RuntimeException e) {
            System.out.println(errorMessage);
        }
        return Optional.empty();
    }

    private Genome getNextMutant() {
        while (true) {
            final var genome = genomeOperations.mutate(circuit.genome, c / genomeOperations.genesNumber);
            final var diff = getDifferentActiveNodes(genome);
            if (!diff.isEmpty() && isValidGenome(genome)) {
                return genome;
            }
        }
//        var currentGenome = circuit.genome;
//        while (true) {
//            currentGenome = genomeOperations.mutate(currentGenome, c / genomeOperations.genesNumber);
//            final var diff = getDifferentActiveNodes(currentGenome);
//            if (!diff.isEmpty() && isValidGenome(currentGenome)) {
//                return currentGenome;
//            }
//        }
    }

    private boolean isValidGenome(final Genome genome) {
        return true;
    }

    private List<Integer> getDifferentActiveNodes(final Genome other) {
        final List<Integer> diff = new ArrayList<>();
        for (final var input : circuit.compactCircuit.getInputs().entrySet()) {
            if (input.getValue() != other.inputs[input.getKey()]) {
                diff.add(input.getKey());
            }
        }
        for (final var module : circuit.compactCircuit.getModules().entrySet()) {
            final int moduleNumber = module.getKey() - circuit.getInputsN();
            if (module.getValue().getType() != other.modules[moduleNumber][0]) {
                diff.add(module.getKey());
                continue;
            }
            for (int i = 0; i < module.getValue().getArguments().size(); i++) {
                if (module.getValue().getArguments().get(i) != other.modules[moduleNumber][i + 1]) {
                    diff.add(module.getKey());
                    break;
                }
            }
        }
        if (circuit.compactCircuit.getOutputSource() != other.output) {
            diff.add(circuit.compactCircuit.getOutputN());
        }
        return diff;
    }

    private void play(final Genome genome) {
        final var prevGenome = circuit.genome;
        circuit.applyGenome(genome);
        try {
            circuit.playFor(seconds);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        circuit.applyGenome(prevGenome);
    }

    private void announceMelodies(final Genome[] genomes) {
        System.out.println("You will hear " + lambda + " different melodies. Choose the most favourite");
        for (int i = 0; i < genomes.length; i++) {
            System.out.println("Melody " + (i + 1) + " playing...");
            play(genomes[i]);
            System.out.println("Melody playing finished");
        }
    }

}