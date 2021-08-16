package cgpmodules.modules;

import cgpmodules.Module;
import cgpmodules.Modules;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.SineOscillatorPhaseModulated;
import com.jsyn.unitgen.UnitGenerator;

public class SineOscPhaseModulated implements Module {
    private final SineOscillatorPhaseModulated oscillator;

    public SineOscPhaseModulated() {
        oscillator = new SineOscillatorPhaseModulated();
    }

    @Override
    public int getInputsNumber() {
        return 3;
    }

    @Override
    public UnitOutputPort getOutput() {
        return oscillator.output;
    }

    @Override
    public UnitInputPort getInput(int port) {
        switch (port) {
            case 0:
                return oscillator.amplitude;
            case 1:
                return oscillator.frequency;
            case 2:
                return oscillator.modulation;
            default:
                throw Modules.invalidArgsNumberError(this, "SineOscillatorPhaseModulated");
        }
    }

    @Override
    public UnitGenerator getUnitGenerator() {
        return oscillator;
    }
}
