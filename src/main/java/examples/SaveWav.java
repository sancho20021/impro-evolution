package examples;

import files.Utils;
import files.formats.Composition;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class SaveWav {

    public static boolean saveToWav(final File wavFile, final Composition composition, final double secondsPerGenome) {
        try {
            composition.saveToWav(wavFile, secondsPerGenome);
            System.out.println("Wav file saved to " + wavFile);
            return true;
        } catch (final IOException e) {
            System.err.println("Exporting wav failed due to I/O error: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        final Scanner in = new Scanner(System.in);
        System.out.println("Enter the composition name (name of a folder in 'src/main/resources/data')");
        final String dirS = in.nextLine();
        System.out.println("Enter duration of one genome playing (in seconds)");
        final double seconds = in.nextDouble();

        final var compositionJson = Utils.getCompositionFile(dirS);
        try {
            final var composition = Utils.readObject(compositionJson, Composition.class);
            final var wavFile = compositionJson.toPath().getParent().resolve("improvisation.wav").toFile();
            saveToWav(wavFile, composition, seconds);
        } catch (final IOException e) {
            System.out.println("Error while reading composition file: " + e.getMessage());
        }
    }
}
