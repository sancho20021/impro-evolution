package cgpmodules.modules;

import cgpmodules.Module;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.UnitBinaryOperator;
import com.jsyn.unitgen.UnitGenerator;

import static cgpmodules.Modules.invalidArgsNumberError;

public class BinaryOperatorM implements Module {
    protected final UnitBinaryOperator operator;

    public BinaryOperatorM(final UnitBinaryOperator operator) {
        this.operator = operator;
    }

    @Override
    public int getInputsNumber() {
        return 2;
    }

    @Override
    public UnitOutputPort getOutput() {
        return operator.output;
    }

    @Override
    public UnitInputPort getInput(int i) {
        switch (i) {
            case 0:
                return operator.inputA;
            case 1:
                return operator.inputB;
            default:
                throw invalidArgsNumberError(this, "Binary operator");
        }
    }

    @Override
    public UnitGenerator getUnitGenerator() {
        return operator;
    }
}
