package algorithm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jsyn.JSyn;
import com.jsyn.util.WaveRecorder;
import files.formats.Composition;
import files.formats.OneMelody;

import java.io.*;
import java.nio.file.Path;

public class Modes {
    public static void playMelody(final File oneMelodyJson, final double seconds) {
        try {
            final Gson gson = new Gson();
            final var melody = gson.fromJson(new FileReader(oneMelodyJson), OneMelody.class);
            MusicCGP.playMelody(melody, seconds);
        } catch (final FileNotFoundException | RuntimeException e) {
            System.err.println("Error while reading file: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void readCompositionAndContinue(final File compositionJson) {
        try {
            final Gson gson = new Gson();
            final var composition = gson.fromJson(new FileReader(compositionJson), Composition.class);
            MusicCGP.pickUpMelody(composition);

        } catch (final FileNotFoundException | RuntimeException e) {
            System.err.println("Error while reading file: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void readCompositionAndPlay(final File compositionJson, final double secondsPerMelody) {
        try {
            final Gson gson = new Gson();
            final var composition = gson.fromJson(new FileReader(compositionJson), Composition.class);
            MusicCGP.playComposition(composition, secondsPerMelody);

        } catch (final FileNotFoundException | RuntimeException e) {
            System.err.println("Error while reading file: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
    }

    private static <T> T readObject(final File file, final Class<T> token) {
        try {
            final Gson gson = new Gson();
            return gson.fromJson(new FileReader(file), token);
        } catch (final FileNotFoundException e) {
            System.err.println("Error while reading file: " + e.getMessage());
            throw new UncheckedIOException(e);
        }
    }

    public static MusicCircuit readMusicCircuit(final File oneMelodyJson) {
        final var melody = readObject(oneMelodyJson, OneMelody.class);
        return new MusicCircuit(melody.circuitInfo, melody.genome);
    }

    public static void printCompactMelody(final File oneMelodyGson) {
        try {
            final var gson = new GsonBuilder().setPrettyPrinting().create();
            final var composition = gson.fromJson(new FileReader(oneMelodyGson), OneMelody.class);
            final var circuit = new MusicCircuit(composition.circuitInfo, composition.genome);
            final var compactGenome = circuit.getGraph();
            System.out.println(gson.toJson(compactGenome));
        } catch (final FileNotFoundException | RuntimeException e) {
            System.err.println("Error while reading file: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void saveToWav(final File oneMelodyJson, final double seconds) throws IOException {
        final Path dir = oneMelodyJson.toPath().getParent();
        final String name = oneMelodyJson.getName();
        final var circuit = readMusicCircuit(oneMelodyJson);
        final var synth = JSyn.createSynthesizer();
        synth.add(circuit);

        final var waveFile = dir.resolve(name + ".wav").toFile();
        final var recorder = new WaveRecorder(synth, waveFile);
        circuit.output.connect(0, recorder.getInput(), 0);
        circuit.output.connect(0, recorder.getInput(), 1);

        synth.start();
        recorder.start();
        try {
            synth.sleepFor(seconds);
        } catch (final InterruptedException e) {
            System.err.println("Recording interrupted: " + e.getMessage());
            e.printStackTrace();
        }
        recorder.stop();
        recorder.close();
        synth.stop();
    }
}
