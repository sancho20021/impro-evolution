package algorithm;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.*;
import files.formats.Composition;
import files.formats.OneMelody;

import java.io.IOException;
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
    private final Logger logger;
    private Genome genome;
    private int pannedTracksN;
    private final Synthesizer synth;

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
        logger = new Logger(circuitInfo);
        synth = JSyn.createSynthesizer();
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

    public static Genome pickUpMelody(final OneMelody oneMelody) {
        final var cgp = new MusicCGP(oneMelody.circuitInfo);
        return cgp.evolve(oneMelody.genome);
    }

    public static Genome pickUpMelody(final Composition composition) {
        final var cgp = new MusicCGP(composition.getCircuitInfo());
        return cgp.evolve(composition.getGenomes().get(composition.getGenomes().size() - 1));
    }

    public static void playComposition(final Composition composition, final double secondsPerMelody) {
        final var cgp = new MusicCGP(composition.getCircuitInfo());
        cgp.setListeningTime(secondsPerMelody);
        for (final var genome : composition.getGenomes()) {
            cgp.playParallel(List.of(genome));
        }
    }

    public static void playMelody(final OneMelody melody, final double secondsToPlay) {
        final var cgp = new MusicCGP(melody.circuitInfo);
        cgp.setListeningTime(secondsToPlay);
        cgp.playParallel(List.of(melody.genome));
    }

    public Genome evolve() {
        return evolve(generateValidGenome());
    }

    private Genome generateValidGenome() {
        while (true) {
            final var genome = genomeOperations.generateGenome();
            if (isValidGenome(genome)) {
                return genome;
            }
        }
    }

    private void listCommands() {
        System.out.println(
                "'help' to see commands.\n" +
                        "'stop' if you are finished with composing.\n" +
                        "'set m <number>' to change mutation rate (from 1 to +inf).\n" +
                        "'set time <number>' to change listening time.\n" +
                        "'set f <number>' to change probability of forward cords (from 0 to 1)'.\n" +
                        "'set f <number>' to change number of simultaneously playing tracks.\n" +
                        "'current' to listen to current melody.\n" +
                        "'repeat' to listen to suggested melodies.\n" +
                        "'save' to save current melody.\n" +
                        "number from 1 to " + lambda + " to choose favourite melody or '0' if you don't like them"
        );
    }

    public Genome evolve(final Genome startGenome) {
        listCommands();
        genome = startGenome;
        logger.addGenome(genome);
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
                    playParallel(List.of(this.genome));
                    System.out.println("Playing stopped. Type command");
                } else if (input.equals("repeat")) {
                    System.out.println("Playing melodies one more time");
                    announceMelodies(offspring);
                } else if (input.equals("save")) {
                    System.out.println("Saving current melody to file " + this.genome.hashCode());
                    try {
                        logger.putObject(
                                Integer.toString(this.genome.hashCode()),
                                new OneMelody(circuitInfo, this.genome)
                        );
                    } catch (final IOException e) {
                        System.err.println("Couldn't save melody: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else if (input.matches("set \\S+ \\S+")) {
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
                        case "p":
                            computeData(
                                    words[2],
                                    Integer::parseInt,
                                    x -> x >= 1,
                                    newP -> {
                                        setPannedTracksN(newP);
                                        System.out.println("p set to " + newP);
                                    },
                                    "p must be positive integer"
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
                logger.addGenome(genome);
            }
        }

        System.out.println("Do you want to save your improvisation? Type 'y' if yes, 'n' otherwise");
        boolean isEnd = false;
        while (!isEnd) {
            final String s = scn.nextLine();
            switch (s) {
                case "y":
                    try {
                        logger.saveToFile();
                        System.out.println("Improvisation saved to " + logger.getDirectoryName() + " directory");
                    } catch (final IOException e) {
                        System.err.println("Couldn't save composition: " + e.getMessage());
                        e.printStackTrace();
                    }
                    isEnd = true;
                    break;
                case "n":
                    isEnd = true;
                    break;
                default:
                    System.out.println("Enter a correct option");
            }
        }
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
        final var synth = JSyn.createSynthesizer();
        synth.setRealTime(false);

        final var circuit = new MusicCircuit(circuitInfo, genome);
        synth.add(circuit);
        final var hi = new FilterHighPass();
        synth.add(hi);
        final var lo = new FilterLowPass();
        synth.add(lo);

        circuit.output.connect(hi.input);
        hi.frequency.set(20);
        hi.output.connect(lo.input);
        lo.frequency.set(20_000);

        final var peakFollower = new PeakFollower();
        synth.add(peakFollower);
        lo.output.connect(peakFollower.input);

        synth.start(20000);

        final double step = peakFollower.halfLife.get();
        double averageAmp = 0;
        int n = 0;
        peakFollower.start();
        for (double i = 0; i < seconds; i += step) {
            try {
                synth.sleepFor(step);
            } catch (final InterruptedException e) {
                System.err.println("Music genome loudness check failed: " + e.getMessage());
                return true;
            }
            averageAmp += (peakFollower.output.get() - averageAmp) / (n + 1);  // A_{n+1} = A_n + (x_{n+1} - A_n)/(n+1)
            n++;
        }

        synth.stop();

        final double db = com.softsynth.math.AudioMath.amplitudeToDecibels(averageAmp);
        return db > -40;
    }

    private List<Integer> getDifferentActiveNodes(final Genome other) {
        final List<Integer> diff = new ArrayList<>();
        for (final var module : new MusicCircuit(circuitInfo, genome).getModules().entrySet()) {
            final int index = module.getKey();
            if (circuitInfo.isInput(index) && genome.inputs[index] != other.inputs[index]) {
                diff.add(index);
            } else if (circuitInfo.isModule(index)) {
                final int moduleIndex = index - circuitInfo.inputsN;
                final int argsNumber = module.getValue().getArguments().size();
                for (int i = 0; i <= argsNumber; i++) {
                    if (genome.modules[moduleIndex][i] != other.modules[moduleIndex][i]) {
                        diff.add(index);
                    }
                }
            }
        }
        if (genome.output != other.output) {
            diff.add(genome.inputs.length + genome.modules.length);
        }
        return diff;
    }

    private void playParallel(List<Genome> genomes) {
        final var circuits = genomes.stream().map(g -> new MusicCircuit(circuitInfo, g)).collect(Collectors.toList());
        final var out = new LineOut();
        if (circuits.size() == 1) {
            circuits.get(0).getOutput().connect(0, out.getInput(), 0);
            circuits.get(0).getOutput().connect(0, out.getInput(), 1);
            play(circuits, out, seconds);
            return;
        }
        final List<UnitGenerator> units = new ArrayList<>(circuits);
        for (int i = 0; i < circuits.size(); i++) {
            final var pan = new Pan();
            circuits.get(i).getOutput().connect(pan.input);
            pan.pan.set(-1 + 2.0 / (circuits.size() - 1) * i);
            pan.output.connect(0, out.input, 0);
            pan.output.connect(1, out.input, 1);
            units.add(pan);
        }
        play(units, out, seconds);
    }

    // TODO playing current multiple times leads to different sounds
    public void play(final List<? extends UnitGenerator> units, final LineOut out, final double secondsToPlay) {
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
            units.forEach(synth::remove);
            synth.remove(out);
        }
    }

    private void announceMelodies(final Genome[] genomes) {
        System.out.println("You will hear " + lambda + " different melodies. Choose the most favourite");
        final var genomesList = Arrays.stream(genomes).collect(Collectors.toList());
        for (int groupI = 0; groupI < (genomes.length + pannedTracksN - 1) / pannedTracksN; groupI++) {
            final int from = groupI * pannedTracksN, to = Math.min(genomesList.size(), (groupI + 1) * pannedTracksN);
            System.out.println("Melodies " + (from + 1) + "..." + to + " playing...");
            playParallel(genomesList.subList(from, to));
            System.out.println("Melodies playing finished");
        }
    }

}
