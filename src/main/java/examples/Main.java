package examples;

import algorithm.MusicCGP;
import algorithm.CircuitInfo;

public class Main {

    public static void evolveMusic() {
        final var circuitInfo = new CircuitInfo(10, 3, 10, 2);
        final var cgp = new MusicCGP(circuitInfo);
        cgp.setForwardCordsPr(0.1);
        cgp.setPannedTracksN(2);
        cgp.evolve();
    }

    public static void main(String[] args) {
        evolveMusic();
    }
}
