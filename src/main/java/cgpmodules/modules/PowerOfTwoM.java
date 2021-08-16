package cgpmodules.modules;

import cgpmodules.Module;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.PowerOfTwo;
import com.jsyn.unitgen.UnitGenerator;

import static cgpmodules.Modules.invalidArgsNumberError;

public class PowerOfTwoM implements Module {
    private final PowerOfTwo powerOfTwo;

    public PowerOfTwoM() {
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