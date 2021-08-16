package cgpmodules;

import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;

import java.util.Arrays;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;

public class PitchQuantizer extends Quantizer {
    public final static double A4_STANDARD = 440;
    public final static double SEMITONE = Math.pow(2, 1.0 / 12);
    private final double a4;
    private final NavigableSet<Double> freqs;
    public UnitInputPort input;
    public UnitOutputPort output;

    public PitchQuantizer(final double a4, final boolean[] allowedNotes) {
        super();
        if (!(20 <= a4 && a4 <= 20_000 && allowedNotes.length == 12)) {
            throw new IllegalArgumentException("(20 <= a4 && a4 <= 20_000 && allowedNotes == 12) condition not satisfied");
        }
        this.a4 = a4;
        freqs = new TreeSet<>();
        double f = a4;
        while (f <= 20_000) {
            double curF = f;
            for (int i = 0; i < 12 && curF <= 20_000; i++) {
                if (allowedNotes[i]) {
                    freqs.add(curF);
                }
                curF *= SEMITONE;
            }
            f *= 2;
        }
        f = a4;
        while (f >= 20) {
            double curF = f;
            for (int i = 11; i >= 0 && curF > 0; i--) {
                if (allowedNotes[i]) {
                    freqs.add(curF);
                }
                curF /= SEMITONE;
            }
            f /= 2;
        }
    }

    public PitchQuantizer(final boolean[] allowedNotes) {
        this(A4_STANDARD, allowedNotes);
    }

    @Override
    protected double get(double input) {
        return Objects.requireNonNullElseGet(freqs.floor(input), () -> freqs.ceiling(input));
    }

    public double getA4() {
        return a4;
    }

    public static PitchQuantizer getMinorStandard() {
        return new PitchQuantizer(new boolean[]{
                true, false, true, true, false, true, false, true, true, false, true, false
        });
    }

    public static PitchQuantizer getMajorStandard() {
        return new PitchQuantizer(new boolean[]{
                true, false, true, false, true, true, false, true, false, true, false, true
        });
    }

    public static PitchQuantizer getChromaticStandard() {
        final boolean[] a = new boolean[12];
        Arrays.fill(a, true);
        return new PitchQuantizer(a);
    }
}
