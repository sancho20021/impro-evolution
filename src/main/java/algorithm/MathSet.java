package algorithm;

public abstract class MathSet<T> {
    abstract String getString();

    abstract boolean isInSet(T x);

    @Override
    public String toString() {
        return getString();
    }
}
