package examples;

import algorithm.MusicCGP;
import files.Utils;
import files.formats.Composition;

import java.io.IOException;
import java.util.Scanner;

public class ContinueSaved {
    public static void main(String[] args) {
        final Scanner in = new Scanner(System.in);
        System.out.println("Enter the composition name (name of a folder in 'src/main/resources/data')");
        final String dirS = in.nextLine();
        final var compositionFile = Utils.getCompositionFile(dirS);
        try {
            final Composition composition = Utils.readObject(compositionFile, Composition.class);
            MusicCGP.pickUpMelody(composition);
        } catch (final IOException e) {
            System.out.println("Error while reading composition file: " + e.getMessage());
        }
    }
}
