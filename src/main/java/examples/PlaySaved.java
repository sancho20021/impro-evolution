package examples;

import algorithm.Modes;
import com.jsyn.unitgen.FilterHighPass;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.Maximum;
import com.jsyn.unitgen.SineOscillator;
import examples.manual.Common;
import modules.UnitConstant;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class PlaySaved {
    public final static Path directory = Path.of("src/main/resources/data");

    public static File getCompFile(final String name) {
        return directory.resolve(name).resolve("long").toFile();
    }

    @Test
    public void test01() {
        Modes.readCompositionAndPlay(getCompFile("1627388596932"), 4);
    }

}
