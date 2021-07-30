package files.formats;

import algorithm.CircuitInfo;
import algorithm.MusicCircuit;

import java.util.List;

public class CompactComposition {
    public final CircuitInfo circuitInfo;
    public final List<MusicCircuit.Graph> graphs;

    public CompactComposition(final CircuitInfo circuitInfo, final List<MusicCircuit.Graph> graphs) {
        this.circuitInfo = circuitInfo;
        this.graphs = graphs;
    }
}
