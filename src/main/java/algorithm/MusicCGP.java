package algorithm;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.*;
import examples.SaveWav;
import files.formats.Composition;
import files.formats.OneMelody;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MusicCGP {
    private final CircuitInfo circuitInfo;
    private final GenomeOperations genomeOperations;
    public final int lambda;
    private int maxPopulations = Integer.MAX_VALUE;
    private final Scanner scn = new Scanner(System.in);

    private double seconds = 2;
    private final Logger logger;
    private Genome genome;
    private int pannedTracksN;
    private final Synthesizer synth;

    private static final DoubleRange TIME_RANGE = new DoubleRange(0, Double.MAX_VALUE, false, true);
    private static final IntRange PAN_RANGE = new IntRange(1, Integer.MAX_VALUE, true, true);


    public MusicCGP(final CircuitInfo info, final int lambda, final double mu) {
        this(info, lambda, mu, 0);
    }

    public MusicCGP(final CircuitInfo info, final int lambda, final double mu, final double forwardCordsPr) {
        this.circuitInfo = info;
        genomeOperations = new GenomeOperations(circuitInfo, new Random(2));
        genomeOperations.setF(forwardCordsPr);
        this.lambda = lambda;
        genomeOperations.setC(mu);
        this.pannedTracksN = 1;
        logger = new Logger(circuitInfo);
        synth = JSyn.createSynthesizer();
    }

    public MusicCGP(final CircuitInfo info) {
        this(info, 4, 2);
    }

    public boolean setForwardCordsPr(final double x) {
        return genomeOperations.setF(x);
    }

    public boolean setSeconds(final double seconds) {
        if (TIME_RANGE.isInSet(seconds)) {
            this.seconds = seconds;
            return true;
        } else {
            return false;
        }
    }

    public boolean setPannedTracksN(final int n) {
        if (PAN_RANGE.isInSet(n)) {
            pannedTracksN = n;
            return true;
        }
        return false;
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
                        "'set p <number>' to change number of simultaneously playing tracks.\n" +
                        "'set d <number>' to change real mutations to int mutations proportion.\n" +
                        "'get <parameter>' to see current parameter value.\n" +
                        "'current' to listen to current melody.\n" +
                        "'repeat' to listen to suggested melodies.\n" +
                        "'save' to save current melody.\n" +
                        "'play' to play current composition from start to end. Each melody will be played for <time> seconds.\n" +
                        "number from 1 to " + lambda + " to choose favourite melody or '0' if you don't like them"
        );
    }

    public Genome evolve(final Genome startGenome) {
        listCommands();
        genome = startGenome;
        logger.addGenome(genome);
        final Map<String, Supplier<? extends Number>> param_getters = Map.of(
                "m", genomeOperations::getC,
                "time", () -> seconds,
                "f", genomeOperations::getF,
                "p", () -> pannedTracksN,
                "d", genomeOperations::getD
        );
        outer:
        for (int i = 0; i < maxPopulations; i++) {
            final var offspring = new Genome[lambda];
            for (int j = 0; j < offspring.length; j++) {
                offspring[j] = getNextMutant(genome);
            }
            announceMelodies(offspring);

            AtomicInteger trackNumber = new AtomicInteger(-1);
            while (trackNumber.get() == -1) {
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
                    final String name = Integer.toString(genome.hashCode());
                    System.out.println("Saving current melody to file " + name);
                    try {
                        logger.putObject(
                                name,
                                new OneMelody(circuitInfo, this.genome)
                        );
                        if (askYN("Do you want to export it to wav?")) {
                            final File wavFile = logger.getDirectory().resolve(name + ".wav").toFile();
                            final var comp = new Composition(circuitInfo, List.of(genome));
                            SaveWav.saveToWav(wavFile, comp, seconds);
                        } else {
                            System.out.println("Ok. Enter a command");
                        }
                    } catch (final IOException e) {
                        System.err.println("Couldn't save melody: " + e.getMessage());
                    }
                } else if (input.equals("play")) {
                    final Composition composition = logger.getComposition();
                    System.out.println(
                            "Playing current composition. It will be played for " +
                                    +composition.getGenomes().size() * seconds + " seconds."
                    );
                    playComposition(composition, seconds);
                    System.out.println("Playing finished.");
                } else if (input.matches("set \\S+ \\S+")) {
                    final var words = input.split("\\s");
                    switch (words[1]) {
                        case "m":
                            tryToSetParameter("mutation", words[2], Double::parseDouble, genomeOperations::setC, genomeOperations.getCRange());
                            break;
                        case "time":
                            tryToSetParameter("time", words[2], Double::parseDouble, this::setSeconds, TIME_RANGE);
                            break;
                        case "f":
                            tryToSetParameter("f", words[2], Double::parseDouble, genomeOperations::setF, genomeOperations.getFRange());
                            break;
                        case "p":
                            tryToSetParameter("panned tracks number", words[2], Integer::parseInt, this::setPannedTracksN, PAN_RANGE);
                            break;
                        case "d":
                            tryToSetParameter("d", words[2], Double::parseDouble, genomeOperations::setD, genomeOperations.getDRange());
                            break;
                        default:
                            System.out.println("No such parameter '" + words[1] + "'");
                    }
                } else if (input.matches("get \\S+")) {
                    final var words = input.split("\\s");
                    if (param_getters.containsKey(words[1])) {
                        System.out.println(words[1] + " = " + param_getters.get(words[1]).get());
                    } else {
                        System.out.println("No such parameter '" + words[1] + "'");
                    }
                } else {
                    try {
                        final int x = Integer.parseInt(input);
                        if (0 <= x && x <= offspring.length) {
                            trackNumber.set(x);
                        } else {
                            System.out.println("Unrecognized command");
                        }
                    } catch (final NumberFormatException e) {
                        System.out.println("Unrecognized command");
                    }
                }
            }
            if (trackNumber.get() > 0) {
                genome = offspring[trackNumber.get() - 1];
                logger.addGenome(genome);
            }
        }

        if (askYN("Do you want to save your improvisation?")) {
            try {
                logger.saveToFile();
                System.out.println("Improvisation saved to " + logger.getDirectoryName() + " directory");
            } catch (final IOException e) {
                System.err.println("Couldn't save composition: " + e.getMessage());
            }
            if (askYN("Do you want to export your improvisation to wav?")) {
                try {
                    logger.ensureDirectoryExists();
                    final var comp = logger.getComposition();
                    final var wavFile = logger.getDirectory().resolve("improvisation.wav").toFile();
                    SaveWav.saveToWav(wavFile, comp, seconds);
                } catch (final IOException e) {
                    System.err.println("Couldn't export to wav: " + e.getMessage());
                }
            }
        }
        return genome;
    }

    private <T> void tryToSetParameter(
            final String parameterName,
            final String s,
            final Function<String, T> f,
            final Function<T, Boolean> setter,
            final MathSet<T> set
    ) {
        try {
            final T t = f.apply(s);
            if (setter.apply(t)) {
                System.out.println(parameterName + " set to " + t);
            } else {
                System.out.println("Parameter " + parameterName + " must be in " + set);
            }
        } catch (final RuntimeException e) {
            System.out.println("Parameter " + parameterName + " must be in " + set);
        }
    }

    private Genome getNextMutant(final Genome genome) {
        final var currentCircuit = new MusicCircuit(circuitInfo, genome);
        while (true) {
            final var nextGenome = genomeOperations.mutate(genome);
            final var diff = getDifferentActiveNodes(currentCircuit, nextGenome);
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
                Thread.currentThread().interrupt();
                return true;
            }
            averageAmp += (peakFollower.output.get() - averageAmp) / (n + 1);  // A_{n+1} = A_n + (x_{n+1} - A_n)/(n+1)
            n++;
        }

        synth.stop();

        final double db = com.softsynth.math.AudioMath.amplitudeToDecibels(averageAmp);
        return db > -20;
    }

    private List<Integer> getDifferentActiveNodes(final MusicCircuit currentCircuit, final Genome other) {
        final List<Integer> diff = new ArrayList<>();
        for (final var module : currentCircuit.getGraph().modules.entrySet()) {
            final int index = module.getKey();
            final int moduleIndex = index - circuitInfo.inputsN;
            final int argsNumber = module.getValue().getArguments().size();
            for (int i = 0; i <= argsNumber; i++) {
                if (genome.modules[moduleIndex][i] != other.modules[moduleIndex][i]) {
                    diff.add(index);
                }
            }
        }
        for (final var index : currentCircuit.getGraph().inputs.keySet()) {
            if (circuitInfo.isInput(index) && genome.inputs[index] != other.inputs[index]) {
                diff.add(index);
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

    private boolean askYN(final String message) {
        System.out.println(message);
        System.out.println("Type 'y' if yes, 'n' otherwise");
        while (true) {
            final String s = scn.nextLine();
            switch (s) {
                case "y":
                    return true;
                case "n":
                    return false;
                default:
                    System.out.println("Enter a correct option");
            }
        }
    }

}
