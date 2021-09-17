package algorithm;

public class Pair<T> {
    private final T a, b;

    public Pair(final T a, final T b) {
        this.a = a;
        this.b = b;
    }

    public T getA() {
        return a;
    }

    public T getB() {
        return b;
    }

    public static class IntPair {
        private final int a, b;

        public IntPair(final int a, final int b) {
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public int getB() {
            return b;
        }
    }

    public static class DoublePair {
        protected final double a, b;

        public DoublePair(final double a, final double b) {
            this.a = a;
            this.b = b;
        }

        public double getA() {
            return a;
        }

        public double getB() {
            return b;
        }
    }
}
