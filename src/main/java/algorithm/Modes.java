package algorithm;

import algorithm.formats.Composition;
import algorithm.formats.OneMelody;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Modes {
    public static void readMelodyAndContinue(final File oneMelodyJson) {
        try {
            final Gson gson = new Gson();
            final var melody = gson.fromJson(new FileReader(oneMelodyJson), OneMelody.class);
            MusicCGP.pickUpMelody(melody);
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
}
