package cgpmodules.modules;

import cgpmodules.Module;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.UnitGenerator;
import com.jsyn.unitgen.UnitOscillator;

import java.util.List;

import static cgpmodules.Modules.invalidArgsNumberError;

public class Oscillator implements Module {
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
