package algorithm;

import algorithm.formats.Composition;
import algorithm.formats.OneMelody;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

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
}
