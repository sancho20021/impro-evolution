package algorithm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import files.Utils;
import files.formats.CompactComposition;
import files.formats.Composition;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static files.Utils.dataDir;

public class Logger {
    private final Composition composition;
    private Path directory;
    private final Gson gson;

    public Logger(final CircuitInfo circuitInfo) {
        this.composition = new Composition(circuitInfo);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void ensureDirectoryExists() throws IOException {
        if (directory == null) {
            directory = dataDir.resolve(Long.toString(System.currentTimeMillis()));
            Files.createDirectories(directory);
        }
    }

    public String getDirectoryName() {
        return directory.toString();
    }
    public Path getDirectory() {
        return directory;
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
        final List<MusicCircuit.Graph> graphs = new ArrayList<>();
        for (final Genome genome : composition.getGenomes()) {
            graphs.add(new MusicCircuit(circuitInfo, genome).getGraph());
        }
        putString("short", gson.toJson(new CompactComposition(circuitInfo, graphs)));
    }

    public Composition getComposition() {
        return composition;
    }
}
