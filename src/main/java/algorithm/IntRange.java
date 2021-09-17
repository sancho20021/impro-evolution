package algorithm;

public class IntRange extends MathSet<Integer> {
    private final int a;
    private final int b;
    private final boolean fromInclusive;
    private final boolean toInclusive;

    public IntRange(final int a, final int b, final boolean fromInclusive, final boolean toInclusive) {
        this.a = a;
        this.b = b;
        this.fromInclusive = fromInclusive;
        this.toInclusive = toInclusive;
    }

    @Override
    public String getString() {
        return (fromInclusive ? "[" : "(") + a + " : " + (toInclusive ? "]" : ")");
    }

    @Override
    public boolean isInSet(Integer x) {
        return (a <= x && x <= b && (a < x || fromInclusive) && (x < b || toInclusive));
    }
}
