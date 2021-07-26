package algorithm;

import algorithm.MusicCircuit;

public class CircuitInfo {
    public final int inputsN, rows, columns, l, maxArity;

    public CircuitInfo(final int inputsN, final int rows, final int columns, final int l, final int maxArity) {
        this.inputsN = inputsN;
        this.rows = rows;
        this.columns = columns;
        this.l = l;
        this.maxArity = maxArity;
    }

    public CircuitInfo(final int inputsN, final int rows, final int columns, final int l) {
        this(inputsN, rows, columns, l, MusicCircuit.getMaxArity());
    }

    public int getModulesN() {
        return rows * columns;
    }

    public boolean isInput(final int n) {
        return 0 <= n && n < inputsN;
    }

    public boolean isModule(final int n) {
        return inputsN <= n && n - inputsN < getModulesN();
    }

    public boolean isLineOut(final int n) {
        return n == inputsN + getModulesN();
    }
}
