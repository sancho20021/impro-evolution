package algorithm.alternative;

import algorithm.formats.CircuitInfo;

public class Test {
    public static void main(String[] args) {
        final var circuitInfo = new CircuitInfo(10, 2, 10, 2);
        final var cgp = new MusicCGP(circuitInfo);
        cgp.setPannedTracksN(3);
        cgp.evolve();
    }
}
