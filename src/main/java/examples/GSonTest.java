package examples;

import com.google.gson.Gson;
import algorithm.Genome;
import algorithm.GenomeOperations;
import algorithm.MusicCircuit;

import java.util.Random;

public class GSonTest {
    public static void main(String[] args) {
        final var circuit = new MusicCircuit(3, 2, 5, 2);
        final var genomeOps = new GenomeOperations(circuit, new Random(2));
        final var genome = genomeOps.generateGenome();

        final var gson = new Gson();
        final String json = gson.toJson(genome);
        System.out.println(json);
        final var decoded = gson.fromJson(json, Genome.class);
        System.out.println(gson.toJson(decoded));
    }
}
