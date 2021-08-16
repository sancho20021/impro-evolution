package cgpmodules.modules;

import cgpmodules.Module;
import cgpmodules.Modules;
import cgpmodules.Quantizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.UnitGenerator;

public class QuantizerM implements Module {
    private final Quantizer quantizer;

    public QuantizerM(final Quantizer quantizer) {
        this.quantizer = quantizer;
    }

    @Override
    public int getInputsNumber() {
        return 1;
    }

    @Override
    public UnitOutputPort getOutput() {
        return quantizer.getOutput();
    }

    @Override
    public UnitInputPort getInput(int port) {
        if (port == 0) {
            return quantizer.getInput();
        } else {
            throw Modules.invalidArgsNumberError(this, "PitchQuantizer");
        }
    }

    @Override
    public UnitGenerator getUnitGenerator() {
        return quantizer;
    }
}
