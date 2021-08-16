package cgpmodules;

import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.UnitGenerator;

import java.util.List;

public interface Module {
    int getInputsNumber();

    UnitOutputPort getOutput();

    UnitInputPort getInput(int port);

    UnitGenerator getUnitGenerator();

}
