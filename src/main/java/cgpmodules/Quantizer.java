package cgpmodules;

import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.UnitGenerator;
import com.jsyn.unitgen.UnitSink;
import com.jsyn.unitgen.UnitSource;

public abstract class Quantizer extends UnitGenerator implements UnitSink, UnitSource {
    public UnitInputPort input;
    public UnitOutputPort output;

    public Quantizer() {
        addPort(input = new UnitInputPort("Input"));
        addPort(output = new UnitOutputPort("Output"));
    }

    @Override
    public void generate(int start, int stop) {
        final var inputValues = input.getValues();
        final var outputValues = output.getValues();
        for (int i = start; i < stop; i++) {
            outputValues[i] = get(inputValues[i]);
        }
    }

    @Override
    public UnitInputPort getInput() {
        return input;
    }

    @Override
    public UnitOutputPort getOutput() {
        return output;
    }

    protected abstract double get(final double input);

    public static class ToIntQuantizer extends Quantizer {

        @Override
        protected double get(double input) {
            return (int) input;
        }
    }

    /**
     * output = (int) max(0, input)
     */
    public static class ToNaturalQuantizer extends Quantizer {

        @Override
        protected double get(double input) {
            return (int) Math.max(0, input);
        }
    }
}
