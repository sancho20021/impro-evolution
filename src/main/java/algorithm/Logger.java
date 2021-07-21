package algorithm;

import algorithm.formats.CompactComposition;
import algorithm.formats.Composition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Logger {
    private final MusicCircuit circuit;
    private final Composition composition;
    public static final Path dataDir = Path.of("src/main/resources/data");
    private Path directory;
    private final Gson gson;

    public Logger(final MusicCircuit circuit) {
        this.circuit = circuit;
        this.composition = new Composition(circuit.getInputsN(), circuit.rows, circuit.columns, circuit.l, circuit.maxArity);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void ensureDirectoryExists() throws IOException {
        if (directory == null) {
            directory = dataDir.resolve(Long.toString(System.currentTimeMillis()));
            Files.createDirectories(directory);
        }
    }

    public void addGenome(final Genome genome) {
        composition.addGenome(genome);
    }

    public <T> void putObject(final String name, final T object) throws IOException {
        putString(name, gson.toJson(object));
    }

    public void putString(final String name, final String data) throws IOException {
        ensureDirectoryExists();
        try (final PrintWriter writer = new PrintWriter(new FileWriter(directory.resolve(name).toFile()))) {
            writer.print(data);
        }
    }

    public void saveToFile() throws IOException {
        putString("long", gson.toJson(composition));
        final var circuitInfo = composition.getCircuitInfo();
        final List<MusicCircuit.CompactCircuit> compactComposition = new ArrayList<>();
        for (final Genome genome : composition.getGenomes()) {
            circuit.applyGenome(genome);
            compactComposition.add(MusicCircuit.CompactCircuit.fromMusicCircuit(circuit));
        }
        putString("short", gson.toJson(new CompactComposition(circuitInfo, compactComposition)));
    }
}
