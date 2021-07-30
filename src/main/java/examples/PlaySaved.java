package examples;

import algorithm.Modes;
import com.jsyn.unitgen.FilterHighPass;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.Maximum;
import com.jsyn.unitgen.SineOscillator;
import examples.manual.Common;
import files.Constants;
import modules.UnitConstant;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class PlaySaved {

    public static File getCompFile(final String name) {
        return Constants.dataDir.resolve(name).resolve("long").toFile();
    }

    @Test
    public void test01() {
        Modes.readCompositionAndPlay(getCompFile("1627388596932"), 4);
    }
    @Test
    public void test02_eliminating_silence() {
        Modes.readCompositionAndPlay(getCompFile("1627508349389"), 4);
    }
    @Test
    public void test02_play_one() {
        Modes.playMelody(Constants.dataDir.resolve("1627508349389").resolve("song").toFile(), 25);
    }
    @Test
    public void test03_frying_potato() {
        Modes.readCompositionAndPlay(getCompFile("frying potato"), 4);
    }

}
