package cgpmodules.modules;

import cgpmodules.Module;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.Select;
import com.jsyn.unitgen.UnitGenerator;

import static cgpmodules.Modules.invalidArgsNumberError;

public class SelectM implements Module {
    private final Select select;

    public SelectM() {
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
