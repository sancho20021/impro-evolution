package modules;

import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.*;

public class Modules {
    private Modules() {
    }

    public static class Oscillator implements Module {
        private final UnitOscillator oscillator;

        public Oscillator(final UnitOscillator oscillator) {
            this.oscillator = oscillator;

        }

        @Override
        public int getInputsNumber() {
            return 2;
        }

        @Override
        public UnitOutputPort getOutput() {
            return oscillator.output;
        }

        @Override
        public UnitInputPort getInput(int i) {
            switch (i) {
                case 0:
                    return oscillator.amplitude;
                case 1:
                    return oscillator.frequency;
                default:
                    throw invalidArgsNumberError(this, "Oscillator");
            }
        }

        @Override
        public UnitGenerator getUnitGenerator() {
            return oscillator;
        }
    }

    public static class BinaryOperator implements Module {
        private final UnitBinaryOperator operator;

        public BinaryOperator(final UnitBinaryOperator operator) {
            this.operator = operator;
        }

        @Override
        public int getInputsNumber() {
            return 2;
        }

        @Override
        public UnitOutputPort getOutput() {
            return operator.output;
        }

        @Override
        public UnitInputPort getInput(int i) {
            switch (i) {
                case 0:
                    return operator.inputA;
                case 1:
                    return operator.inputB;
                default:
                    throw invalidArgsNumberError(this, "Binary operator");
            }
        }

        @Override
        public UnitGenerator getUnitGenerator() {
            return operator;
        }
    }

    public static class LineOutModule implements Module {
        private final LineOut lineOut;

        public LineOutModule(final LineOut lineOut) {
            this.lineOut = lineOut;
        }

        public LineOutModule() {
            this(new LineOut());
        }

        @Override
        public int getInputsNumber() {
            return 1;
        }

        @Override
        public UnitOutputPort getOutput() {
            throw new UnsupportedOperationException("LineOut has no output");
        }

        @Override
        public UnitInputPort getInput(int i) {
            if (i == 0) {
                return lineOut.input;
            } else {
                throw invalidArgsNumberError(this, "LineOut");
            }
        }

        @Override
        public UnitGenerator getUnitGenerator() {
            return lineOut;
        }
    }

    public static class PowerOfTwoModule implements Module {
        private final PowerOfTwo powerOfTwo;

        public PowerOfTwoModule() {
            this.powerOfTwo = new PowerOfTwo();
        }

        @Override
        public int getInputsNumber() {
            return 1;
        }

        @Override
        public UnitOutputPort getOutput() {
            return powerOfTwo.output;
        }

        @Override
        public UnitInputPort getInput(int i) {
            if (i == 0) {
                return powerOfTwo.input;
            } else {
                throw invalidArgsNumberError(this, "PowerOfTwo");
            }
        }

        @Override
        public UnitGenerator getUnitGenerator() {
            return powerOfTwo;
        }
    }

    public static class Constant implements Module {
        private final UnitConstant unitConstant;

        public Constant(final UnitConstant unitConstant) {
            this.unitConstant = unitConstant;
        }

        public Constant(final double value) {
            this(new UnitConstant(value));
        }

        @Override
        public int getInputsNumber() {
            return 0;
        }

        @Override
        public UnitOutputPort getOutput() {
            return unitConstant.output;
        }

        @Override
        public UnitInputPort getInput(int i) {
            throw invalidArgsNumberError(this, "Constant");
        }

        @Override
        public UnitGenerator getUnitGenerator() {
            return unitConstant;
        }

        @Override
        public String toString() {
            return Double.toString(unitConstant.value);
        }
    }

    public static class Two implements Module {
        private final UnitConstant constant;

        public Two() {
            constant = new UnitConstant(2);
        }

        @Override
        public int getInputsNumber() {
            return 0;
        }

        @Override
        public UnitOutputPort getOutput() {
            return constant.output;
        }

        @Override
        public UnitInputPort getInput(int i) {
            throw invalidArgsNumberError(this, "Constant");
        }

        @Override
        public UnitGenerator getUnitGenerator() {
            return constant;
        }
    }

    public static class SelectModule implements Module {
        private final Select select;

        public SelectModule() {
            select = new Select();
        }

        @Override
        public int getInputsNumber() {
            return 3;
        }

        @Override
        public UnitOutputPort getOutput() {
            return select.output;
        }

        @Override
        public UnitInputPort getInput(int port) {
            switch (port) {
                case 0:
                    return select.select;
                case 1:
                    return select.inputA;
                case 2:
                    return select.inputB;
                default:
                    throw invalidArgsNumberError(this, "Select");
            }
        }

        @Override
        public UnitGenerator getUnitGenerator() {
            return select;
        }
    }

    public static class TunableFilterModule implements Module {
        private final TunableFilter filter;

        public TunableFilterModule(final TunableFilter filter) {
            this.filter = filter;
        }

        @Override
        public int getInputsNumber() {
            return 2;
        }

        @Override
        public UnitOutputPort getOutput() {
            return filter.output;
        }

        @Override
        public UnitInputPort getInput(int port) {
            switch (port) {
                case 0:
                    return filter.input;
                case 1:
                    return filter.frequency;
                default:
                    throw invalidArgsNumberError(this, "Tunable Filter");
            }
        }

        @Override
        public UnitGenerator getUnitGenerator() {
            return filter;
        }
    }

    public static class LatchModule implements Module {
        private final Latch latch;

        public LatchModule() {
            latch = new Latch();
        }

        @Override
        public int getInputsNumber() {
            return 2;
        }

        @Override
        public UnitOutputPort getOutput() {
            return latch.output;
        }

        @Override
        public UnitInputPort getInput(int port) {
            switch (port) {
                case 0:
                    return latch.input;
                case 1:
                    return latch.gate;
                default:
                    throw invalidArgsNumberError(this, "Latch");
            }
        }

        @Override
        public UnitGenerator getUnitGenerator() {
            return latch;
        }
    }

    private static IllegalArgumentException invalidArgsNumberError(final Module module, final String name) {
        return new IllegalArgumentException(name + " has only " + module.getInputsNumber() + " inputs");
    }
}
