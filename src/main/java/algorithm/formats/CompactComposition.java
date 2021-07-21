package algorithm.formats;

import algorithm.MusicCircuit;

import java.util.List;

public class CompactComposition {
    public final CircuitInfo circuitInfo;
    public final List<MusicCircuit.CompactCircuit> compactComposition;

    public CompactComposition(final CircuitInfo circuitInfo, final List<MusicCircuit.CompactCircuit> compactComposition) {
        this.circuitInfo = circuitInfo;
        this.compactComposition = compactComposition;
    }
}
