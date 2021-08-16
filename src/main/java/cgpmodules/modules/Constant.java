package cgpmodules.modules;

import cgpmodules.Module;
import cgpmodules.UnitConstant;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.UnitGenerator;

import static cgpmodules.Modules.invalidArgsNumberError;

public class Constant implements Module {
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
