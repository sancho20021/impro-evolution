package algorithm.formats;

import algorithm.MusicCircuit;

public class CircuitInfo {
    public final int inputsN, rows, columns, l, maxArity;

    public CircuitInfo(final MusicCircuit circuit) {
        this(circuit.getInputsN(), circuit.rows, circuit.columns, circuit.l, circuit.maxArity);
    }

    public CircuitInfo(final int inputsN, final int rows, final int columns, final int l, final int maxArity) {
        this.inputsN = inputsN;
        this.rows = rows;
        this.columns = columns;
        this.l = l;
        this.maxArity = maxArity;
    }
}
