package modules;

import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.UnitGenerator;

public interface Module {
    int getInputsNumber();
    UnitOutputPort getOutput();
    UnitInputPort getInput(int port);
    UnitGenerator getUnitGenerator();
}
