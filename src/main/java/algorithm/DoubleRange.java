package algorithm;

public class DoubleRange extends MathSet<Double> {
    private final double a;
    private final double b;
    private final boolean fromInclusive;
    private final boolean toInclusive;

    public DoubleRange(final double a, final double b, final boolean fromInclusive, final boolean toInclusive) {
        this.a = a;
        this.b = b;
        this.fromInclusive = fromInclusive;
        this.toInclusive = toInclusive;
    }

    @Override
    public String getString() {
        return (fromInclusive ? "[" : "(") + a + ", " + b + (toInclusive ? "]" : ")");
    }

    @Override
    public boolean isInSet(Double x) {
        return a <= x && x <= b && (fromInclusive || a < x) && (toInclusive || x < b);
    }
}


