package cgpmodules.modules;

import cgpmodules.Module;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.Latch;
import com.jsyn.unitgen.UnitGenerator;

import static cgpmodules.Modules.invalidArgsNumberError;

public  class LatchM implements Module {
    private final Latch latch;

    public LatchM() {
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
