package cgpmodules;

import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.UnitGenerator;
import com.jsyn.unitgen.UnitSource;

import java.util.Arrays;

public class UnitConstant extends UnitGenerator implements UnitSource {
    public final double value;
    public final UnitOutputPort output;

    public UnitConstant(final double value) {
        this.value = value;
        this.addPort(this.output = new UnitOutputPort("Output"));
    }

    @Override
    public void generate(final int start, final int limit) {
        Arrays.fill(output.getValues(), start, limit, value);
    }

    @Override
    public UnitOutputPort getOutput() {
        return output;
    }
}
