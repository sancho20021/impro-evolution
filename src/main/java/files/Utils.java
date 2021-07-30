package files;

import algorithm.MusicCircuit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import files.formats.OneMelody;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class Utils {
    public final static Path dataDir = Path.of("src/main/resources/data");

    public static File getCompositionFile(final String name) {
        return Utils.dataDir.resolve(name).resolve("long").toFile();
    }

    public static <T> T readObject(final File file, final Class<T> token) throws IOException {
        final Gson gson = new Gson();
        return gson.fromJson(new FileReader(file), token);
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
