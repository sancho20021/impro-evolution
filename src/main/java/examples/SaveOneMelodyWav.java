package examples;

import files.Utils;
import files.formats.Composition;
import files.formats.OneMelody;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class SaveOneMelodyWav {
    public static boolean exportMelodyToWav(final String dirName, final String jsonFileName, final double seconds) {
        try {
            final var melody = Utils.readObject(Utils.dataDir.resolve(dirName).resolve(jsonFileName).toFile(), OneMelody.class);
            final var composition = new Composition(melody.circuitInfo, List.of(melody.genome));
            final var wavFile = Utils.dataDir.resolve(dirName).resolve(jsonFileName + ".wav").toFile();
            SaveWav.saveToWav(wavFile, composition, 4);
            return true;
        } catch (final IOException e) {
            System.out.println("Reading melody failed: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        final Scanner in = new Scanner(System.in);
        System.out.println("Enter the melody path, relative to 'src/main/resources/data/'");
        final String fileS = in.nextLine();
        System.out.println("Enter duration in seconds");
        final double seconds = in.nextDouble();
        final Path file = Utils.dataDir.resolve(fileS);
        exportMelodyToWav(file.getParent().getFileName().toString(), file.getFileName().toString(), seconds);
    }
}
