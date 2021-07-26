package algorithm.formats;

import algorithm.CircuitInfo;
import algorithm.Genome;

public class OneMelody {
    public final CircuitInfo circuitInfo;
    public final Genome genome;

    public OneMelody(final CircuitInfo circuitInfo, final Genome genome) {
        this.circuitInfo = circuitInfo;
        this.genome = genome;
    }
}