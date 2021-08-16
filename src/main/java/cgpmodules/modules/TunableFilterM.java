package cgpmodules.modules;

import cgpmodules.Module;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.TunableFilter;
import com.jsyn.unitgen.UnitGenerator;

import static cgpmodules.Modules.invalidArgsNumberError;

public class TunableFilterM implements Module {
    private final TunableFilter filter;

    public TunableFilterM(final TunableFilter filter) {
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
